package de.miraculixx.mcord_event.modules.connect

import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.entities.ButtonEvent
import de.miraculixx.mcord_event.utils.entities.ModalEvent
import dev.minn.jda.ktx.interactions.components.replyModal
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ConnectButton: ButtonEvent, ModalEvent {
    override suspend fun trigger(it: ButtonInteractionEvent) {
        val member = it.member ?: return
        val memberID = member.idLong
        if (ConnectManager.isConnected(memberID)) {
            val user = SQL.getUser(memberID)
            it.reply_("```diff\n- Du bist bereits mit dem Twitch Account '${user.twitchName}' verbunden!```", ephemeral = true).queue()
            return
        }

        it.replyModal("INFO:CONNECT", "Connect Twitch Account") {
            short("TWITCH", "Twitch Name", true) {
                placeholder = "Gebe deinen Account Namen hier ein"
                minLength = 3
            }
        }.queue()
    }

    override suspend fun trigger(it: ModalInteractionEvent) {
        val member = it.member ?: return
        val memberID = member.idLong
        val twitchName = it.getValue("TWITCH")?.asString ?: return
        val code = ConnectManager.getCode(memberID) ?: ConnectManager.generateCode(memberID, twitchName)
        it.reply_(embeds = listOf(
            Embed {
                title = ":key: || **Account Verbindung**"
                description = "Gebe den folgenden Code mit angegebenen Twitch Account in [meinem Chat](https://www.twitch.tv/popout/miraculixxt/chat) ein, um auch in Twitch zu punkten!\n" +
                        "\n" +
                        "**Code:** ||$code||\n**Command:** `!connect <code>`"
            }
        ), ephemeral = true).queue()
    }
}