package de.miraculixx.mcord_event.utils.entities

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent

interface DropDownEvent {
    suspend fun trigger(it: SelectMenuInteractionEvent)
}