package de.miraculixx.mcord_event.modules.commands

import de.miraculixx.mcord_event.JDA
import de.miraculixx.mcord_event.modules.events.EventManager
import de.miraculixx.mcord_event.modules.events.halloween.Creature
import de.miraculixx.mcord_event.modules.events.halloween.data.CreatureType
import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.entities.SlashCommandEvent
import de.miraculixx.mcord_event.utils.enumOf
import dev.minn.jda.ktx.interactions.components.SelectMenu
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.interactions.components.option
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

class AdminCMD : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        when (it.getOption("call")?.asString ?: return) {
            "NET_SHOP" -> {
                val msgID = it.getOption("id")?.asString ?: return
                val message = it.channel.retrieveMessageById(msgID).complete()
                message.editMessageComponents(ActionRow.of(SelectMenu("SHOP:NETS") {
                    placeholder = "Kaufe Fangnetze..."
                    maxValues = 1
                    minValues = 0
                    option("1x Silbernes Netz", "SILVER:30", "Kosten >> 30 Punkte", Emoji.fromFormatted("<:silver_net:1023682513970925699>"))
                    option("5x Silberne Netze", "SILVER5:130", "Kosten >> 130 Punkte (15% Rabatt)", Emoji.fromFormatted("<:silver_net:1023682513970925699>"))
                    option("1x Goldenes Netz", "GOLD:50", "Kosten >> 50 Punkte", Emoji.fromFormatted("<:gold_net:1023594135153807392>"))
                    option("5x Goldenes Netz", "GOLD5:215", "Kosten >> 215 Punkte (15% Rabatt)", Emoji.fromFormatted("<:gold_net:1023594135153807392>"))
                })).queue()
            }

            "ROLE_SHOP" -> {
                val msgID = it.getOption("id")?.asString ?: return
                val message = it.channel.retrieveMessageById(msgID).complete()
                message.editMessageComponents(ActionRow.of(SelectMenu("SHOP:ROLES") {
                    placeholder = "Kaufe Role Cosmetics..."
                    maxValues = 1
                    minValues = 0
                    option("1 Woche Farb-Rolle", "COLOR:150", "Kosten >> 150 Punkte", Emoji.fromUnicode("\uD83D\uDD36"))
                    option("1 Monat Farb-Rolle", "COLOR-M:500", "Kosten >> 500 Punkte (15% Rabatt)", Emoji.fromUnicode("\uD83D\uDD36"))
                    option("1 Woche Listing-Rolle", "LISTING:250", "Kosten >> 250 Punkte", Emoji.fromFormatted("<:Pumpkin:895271226304503858>"))
                    option("1 Monat Listing-Rolle", "LISTING-M:850", "Kosten >> 850 Punkte (15% Rabatt)", Emoji.fromFormatted("<:Pumpkin:895271226304503858>"))
                })).queue()
            }

            "INFO" -> {
                val msgID = it.getOption("id")?.asString ?: return
                val message = it.channel.retrieveMessageById(msgID).complete()
                message.editMessageComponents(
                    ActionRow.of(
                        button("INFO:USER", "Punkte", Emoji.fromFormatted("<:Pumpkin:895271226304503858>"), ButtonStyle.PRIMARY),
                        button("INFO:TWITCH", "Connect Twitch", Emoji.fromFormatted("<:twitch:909185661716795433>"), ButtonStyle.PRIMARY)
                    )
                ).queue()
            }

            "REMOVE_COMPS" -> {
                val msgID = it.getOption("id")?.asString ?: return
                val message = it.channel.retrieveMessageById(msgID).complete()
                message.editMessageComponents().queue()
            }

            "ADD_POINTS" -> {
                val userID = it.getOption("id")?.asLong ?: return
                val amount = it.getOption("number")?.asInt ?: return
                val userData = SQL.getUser(userID)
                SQL.call("UPDATE halloween22 SET Points=Points+$amount WHERE ID=${userData.id}")
                val halloweenData = SQL.getHalloweenData(userID)
                it.reply_("Der User <@$userID> hat jetzt ${halloweenData.points} Punkte!", ephemeral = true).queue()
            }

            "GET_POINTS" -> {
                val userID = it.getOption("id")?.asLong ?: return
                val data = SQL.getHalloweenData(userID)
                it.reply_("Der User <@$userID> hat ${data.points} Punkte", ephemeral = true).queue()
            }

            "SPAWN" -> {
                if (EventManager.currentCreature != null) {
                    it.reply_("Monster ist bereits da!", ephemeral = true).queue()
                } else {
                    val typeName = it.getOption("type")?.asString
                    val type = enumOf(typeName) ?: CreatureType.values().random()
                    val creature = Creature(type)
                    creature.spawn(JDA)
                    EventManager.currentCreature = creature
                    it.reply_("Das Monster ${type.name} wurde gespawned in #just-chatting", ephemeral = true).queue()
                }
            }

            "DELETE" -> {
                EventManager.currentCreature = null
                it.reply_("Gelöscht", ephemeral = true).queue()
            }
        }
    }
}