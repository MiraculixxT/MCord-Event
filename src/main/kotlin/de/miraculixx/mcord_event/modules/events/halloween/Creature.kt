package de.miraculixx.mcord_event.modules.events.halloween

import de.miraculixx.mcord_event.modules.events.halloween.data.CatchNet
import de.miraculixx.mcord_event.modules.events.halloween.data.CreatureType
import de.miraculixx.mcord_event.modules.events.halloween.data.Rarity
import de.miraculixx.mcord_event.utils.api.SQL
import dev.minn.jda.ktx.generics.getChannel
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.send
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.time.Instant

class Creature(private val type: CreatureType) {
    private val spawnTime = Instant.now().toEpochMilli()
    private val users: MutableList<Long> = mutableListOf()
    private var tries = 0

    private val basicOdds: Short
    private val silverOdds: Short

    fun spawn(jda: JDA) {
        val channel = jda.getChannel<MessageChannel>(1023339222515593308) ?: return
        val embedColor = when (type.rarity) {
            Rarity.COMMON -> 0x7d7f83
            Rarity.RARE -> 0x3e64b1
            Rarity.EPIC -> 0x9e3eb1
            Rarity.LEGENDARY -> 0x947013
        }

        channel.send(embeds = listOf(
            Embed {
                image = "https://cdn.mos.cms.futurecdn.net/gmJ2Vf8uuiPio4XKgndm5c-1200-80.jpg.webp"
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
        ).queue()
    }

    suspend fun useCatchNet(net: CatchNet, hook: InteractionHook) {
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
        val chance = when (net) {
            CatchNet.BASIC -> basicOdds
            CatchNet.SILVER -> if (halloweenData.nSilver <= 0) 0 else {
                val userData = SQL.getUser(userSnowflake)
                SQL.call("UPDATE halloween22 SET N_Silver=N_Silver-1 WHERE ID=${userData.id}")
                silverOdds
            }

            CatchNet.GOLD -> if (halloweenData.nGold <= 0) 0 else {
                val userData = SQL.getUser(userSnowflake)
                SQL.call("UPDATE halloween22 SET N_Gold=N_Gold-1 WHERE ID=${userData.id}")
                100
            }
        }

        if (chance == 0.toShort()) {
            hook.editOriginalEmbeds(
                Embed {
                    title = "Oh nein!"
                    description = "Es scheint als hättest du keine ${net.emote} ${net.pronoun} Netze mehr :sadge:\n" +
                            "Erhalte mehr durch **chatten** oder hole dir welche in <#123>"
                    color = 0x444444
                }
            ).queue()
        } else if ((0..100).random() >= chance) {
            
            hook.editOriginalEmbeds(
                Embed {
                    title = "Gefangen!"
                    description = "Du hast ein **${net.pronoun} Netz** geworfen und... ```diff\n+ die Kreatur getroffen!```\n" +
                            "Sie überlässt dir "
                }
            )
            //catch
        } else {
            users.add(userSnowflake)
            tries++
            hook.editOriginalEmbeds(
                Embed {
                    description = "Du hast ein **${net.pronoun} Netz** geworfen, aber... ```diff\n- es hat leider nicht getroffen```\n" +
                            "> Fangchance - $chance% ${net.emote}"
                }
            )
        }
    }

    init {
        when (type.rarity) {
            Rarity.COMMON -> {
                basicOdds = 60
                silverOdds = 80
            }

            Rarity.RARE -> {
                basicOdds = 50
                silverOdds = 75
            }

            Rarity.EPIC -> {
                basicOdds = 35
                silverOdds = 60
            }

            Rarity.LEGENDARY -> {
                basicOdds = 20
                silverOdds = 50
            }
        }
    }
}