package de.miraculixx.mcord_event.modules.commands

import de.miraculixx.mcord_event.utils.entities.SlashCommandEvent
import dev.minn.jda.ktx.messages.reply_
import dev.minn.jda.ktx.messages.send
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class SendCMD: SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        val msg = it.getOption("msg")?.asString ?: return
        it.channel.send(msg).queue()
        it.reply_("```diff\n+ Message sent in ${it.channel.name}!```", ephemeral = true).queue()
    }
}