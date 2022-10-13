package de.miraculixx.mcord_event.modules.commands

import de.miraculixx.mcord_event.utils.entities.ModalEvent
import de.miraculixx.mcord_event.utils.entities.SlashCommandEvent
import dev.minn.jda.ktx.interactions.components.Modal
import dev.minn.jda.ktx.messages.reply_
import dev.minn.jda.ktx.messages.send
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class SendCMD : SlashCommandEvent, ModalEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        val msg = it.getOption("msg")?.asString
        if (msg != null) {
            it.channel.send(msg).queue()
            it.reply_("```diff\n+ Message sent in ${it.channel.name}!```", ephemeral = true).queue()
        } else {
            it.replyModal(Modal("SEND:CONTENT", "Message Content") {
                this.paragraph("CONTENT", "Message Text", true, placeholder = "Enter your text here")
            }).queue()
        }
    }

    override suspend fun trigger(it: ModalInteractionEvent) {
        val msg = it.getValue("CONTENT")?.asString ?: return
        it.channel.send(msg.replace("\\<", "<")).queue()
        it.reply_("```diff\n+ Message sent in ${it.channel.name}!```", ephemeral = true).queue()
    }
}