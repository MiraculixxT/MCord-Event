package de.miraculixx.mcord_event.modules.commands

import de.miraculixx.mcord_event.modules.events.Event
import de.miraculixx.mcord_event.modules.events.EventManager
import de.miraculixx.mcord_event.utils.entities.SlashCommandEvent
import de.miraculixx.mcord_event.utils.enumOf
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ToggleEventCMD: SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        val newEvent = enumOf<Event>(it.getOption("event")?.asString)

        if (newEvent == null) {
            it.reply_("```diff\n- Bitte gebe ein gültiges Event an!```").queue()
            return
        }

        EventManager.activateEvent(newEvent)
        it.reply_("```diff\n+ Das Event ${newEvent.name} wurde erfolgreich aktiviert! Frohes Punkte sammeln!```").queue()
    }
}