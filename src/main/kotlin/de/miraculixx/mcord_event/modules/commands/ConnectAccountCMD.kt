package de.miraculixx.mcord_event.modules.commands

import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.modules.connect.ConnectManager
import de.miraculixx.mcord_event.utils.entities.SlashCommandEvent
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ConnectAccountCMD : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        val twitchName = it.getOption("twitch-name")?.asString ?: return
        val member = it.member ?: return
        val userID = member.idLong

        if (ConnectManager.isConnected(userID)) {
            val user = SQL.getUser(userID)
            it.reply_("```diff\n- Du hast deinen Account bereits mit Twitch verbunden!\n- Twitch Name: ${user.twitchName}```", ephemeral = true).queue()
            return
        }

        val code = if (ConnectManager.hasCode(userID)) ConnectManager.getCode(userID)
        else ConnectManager.generateCode(userID, twitchName)

        it.reply_(embeds = listOf(
            Embed {
                title = ":key: || **Account Verbindung**"
                description = "Gebe den folgenden Code mit angegebenen Twitch Account in [meinem Chat](https://www.twitch.tv/popout/miraculixxt/chat) ein, um auch in Twitch zu punkten!\n" +
                        "\n" +
                        "**Code:** ||$code||"
            }
        ), ephemeral = true).queue()
    }
}