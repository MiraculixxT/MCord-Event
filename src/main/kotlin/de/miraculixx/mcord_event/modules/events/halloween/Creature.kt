package de.miraculixx.mcord_event.modules.events.halloween

import de.miraculixx.mcord_event.modules.events.EventManager
import de.miraculixx.mcord_event.modules.events.halloween.data.CatchNet
import de.miraculixx.mcord_event.modules.events.halloween.data.CreatureType
import de.miraculixx.mcord_event.modules.events.halloween.data.Rarity
import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.extensions.fancy
import dev.minn.jda.ktx.events.getDefaultScope
import dev.minn.jda.ktx.generics.getChannel
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.send
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

class Creature(private val type: CreatureType) {
    private val spawnTime = Instant.now().toEpochMilli()
    private val users: MutableList<Long> = mutableListOf()
    private var tries = 0
    private var isCaught = false

    private val basicOdds: Short
    private val silverOdds: Short

    private var sourceMessage: Message? = null
    private val decay = getDefaultScope().launch {
        delay(15.minutes)
        sourceMessage?.editMessageComponents()?.queue()
        sourceMessage?.editMessageEmbeds(Embed {
            footer {
                color = 0x2F3136
                iconUrl = "https://cdn.discordapp.com/emojis/752537573976440904.webp?size=240&quality=lossless"
                name = "Kreatur ist geflohen... Beeile dich beim fangen!"
            }
        })
    }

    fun spawn(jda: JDA) {
        val channel = jda.getChannel<MessageChannel>(707934121413836831) ?: return
        val embedColor = when (type.rarity) {
            Rarity.COMMON -> 0x7d7f83
            Rarity.RARE -> 0x3e64b1
            Rarity.EPIC -> 0x9e3eb1
            Rarity.LEGENDARY -> 0x947013
        }

        sourceMessage = channel.send(embeds = listOf(
            Embed {
                image = type.imageURL
                color = embedColor
                footer {
                    iconUrl = "https://cdn.discordapp.com/emojis/752537573976440904.webp?size=240&quality=lossless"
                    name = "${type.spawnMessage} \uD83D\uDC40\n" +
                            "Nutze eines deiner Fangnetze, bevor die Kreatur verschwindet!"
                }
            }
        ), components = listOf(
            ActionRow.of(
                button("EVENT:HALLOWEEN:BASIC:${type.name}", "Basic", Emoji.fromFormatted(CatchNet.BASIC.emote), ButtonStyle.SECONDARY),
                button("EVENT:HALLOWEEN:SILVER:${type.name}", "Silber", Emoji.fromFormatted(CatchNet.SILVER.emote), ButtonStyle.PRIMARY),
                button("EVENT:HALLOWEEN:GOLD:${type.name}", "Gold", Emoji.fromFormatted(CatchNet.GOLD.emote), ButtonStyle.PRIMARY)
            )
        )
        ).complete()
    }

    suspend fun useCatchNet(net: CatchNet, hook: InteractionHook) {
        if (isCaught) {
            hook.editOriginal("```diff\n- Da war wohl jemand schneller...\n- Das Monster ist bereits weg```").queue()
            return
        }

        val clickTime = Instant.now().toEpochMilli()
        val fast = clickTime - spawnTime < 1500 // Clicks very fast (sub 1.5s)
        val userSnowflake = hook.interaction.user.idLong

        if (users.contains(userSnowflake)) {
            hook.editOriginal(
                "```diff\n- Bei deinem ersten Fangversuch ist die Kreatur entwischt...\n- (Nur ein Versuch pro Kreatur)```" +
                        if (fast) " Btw... du klickst **seehr** schnell... Und sehr häufig <:Susge:949672033199996978>" else ""
            ).queue()
            return
        }

        val halloweenData = SQL.getHalloweenData(userSnowflake)
        val userData = SQL.getUser(userSnowflake)
        val chance = when (net) {
            CatchNet.BASIC -> basicOdds
            CatchNet.SILVER -> if (halloweenData.nSilver <= 0) 0 else {
                SQL.call("UPDATE halloween22 SET N_Silver=N_Silver-1 WHERE ID=${userData.id}")
                silverOdds
            }

            CatchNet.GOLD -> if (halloweenData.nGold <= 0) 0 else {
                SQL.call("UPDATE halloween22 SET N_Gold=N_Gold-1 WHERE ID=${userData.id}")
                100
            }
        }

        if (chance == 0.toShort()) {
            hook.editOriginalEmbeds(
                Embed {
                    title = "Oh nein!"
                    description = "Es scheint als hättest du keine ${net.emote} ${net.pronoun} Netze mehr <:Sadge:820962368166428683>\n" +
                            "Erhalte mehr durch **chatten** oder hole dir welche in <#1025036161892241408>"
                    color = 0x444444
                }
            ).queue()
        } else if ((0..100).random() <= chance) { //CATCH
            isCaught = true

            decay.cancel()
            sourceMessage?.editMessageComponents()?.queue()
            sourceMessage?.editMessageEmbeds(Embed {
                footer {
                    color = 0x2F3136
                    iconUrl = net.url
                    name = "${type.rarity.name.fancy()} Kreatur wurde von ${hook.interaction.user.asTag} gefangen!"
                }
            })?.queue()
            EventManager.currentCreature = null

            SQL.call("UPDATE halloween22 SET " +
                when (type.rarity) {
                    Rarity.COMMON -> "C_Common=C_Common+1"
                    Rarity.RARE -> "C_Rare=C_Rare+1"
                    Rarity.EPIC -> "C_Epic=C_Epic+1"
                    Rarity.LEGENDARY -> "C_Legendary=C_Legendary+1"
                } + " WHERE ID=${userData.id}"
            )

            val points = type.rarity.candy
            SQL.call("UPDATE halloween22 SET Points=Points+$points WHERE ID=${userData.id}")
            hook.editOriginalEmbeds(
                Embed {
                    color = 0x0ADA00
                    title = "Gefangen!"
                    description = "Du hast ein **${net.pronoun} Netz** geworfen und... ```diff\n+ die Kreatur getroffen!```\n" +
                            "Sie überlässt dir :candy: **${type.rarity.candy}** Punkte!\n> Fangchance - $chance% ${net.emote}"
                }
            ).queue()

        } else {
            users.add(userSnowflake)
            tries++
            hook.editOriginalEmbeds(
                Embed {
                    color = 0xDA0000
                    description = "Du hast ein **${net.pronoun} Netz** geworfen, aber... ```diff\n- es hat leider nicht getroffen```\n" +
                            "> Fangchance - $chance% ${net.emote}"
                }
            ).queue()
        }
    }

    init {
        decay
        when (type.rarity) {
            Rarity.COMMON -> {
                basicOdds = 50
                silverOdds = 80
            }

            Rarity.RARE -> {
                basicOdds = 40
                silverOdds = 75
            }

            Rarity.EPIC -> {
                basicOdds = 25
                silverOdds = 60
            }

            Rarity.LEGENDARY -> {
                basicOdds = 15
                silverOdds = 50
            }
        }
    }
}