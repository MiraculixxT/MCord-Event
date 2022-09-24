package de.miraculixx.mcord_event.modules.events

import de.miraculixx.mcord_event.modules.events.halloween.Spawner
import de.miraculixx.mcord_event.utils.entities.EventListener

object EventManager {
    private var activeEvent = Event.NONE
    private val listener = mutableListOf<EventListener>()

    fun getEvent(): Event {
        return activeEvent
    }

    fun activateEvent(event: Event): Boolean {
        return if (activeEvent == event) false
        else {
            listener.forEach { it.stopListen() }
            listener.clear()
            when (event) {
                Event.NONE -> {}

                Event.HALLOWEEN -> {
                    listener.addAll(listOf(Spawner()))
                }
            }
            true
        }
    }
}