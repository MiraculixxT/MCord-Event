package de.miraculixx.mcord_event.modules.events

import de.miraculixx.mcord_event.modules.events.halloween.Creature
import de.miraculixx.mcord_event.modules.events.halloween.Spawner
import de.miraculixx.mcord_event.utils.entities.EventListener
import de.miraculixx.mcord_event.utils.log.Color
import de.miraculixx.mcord_event.utils.log.log

object EventManager {
    private var activeEvent = Event.NONE
    private val listener = mutableListOf<EventListener>()

    // Event specific
    var currentCreature: Creature? = null

    fun getEvent(): Event {
        return activeEvent
    }

    fun activateEvent(event: Event): Boolean {
        return if (activeEvent == event) false
        else {
            listener.forEach { it.stopListen() }
            listener.clear()
            activeEvent = event
            when (event) {
                Event.NONE -> {}

                Event.HALLOWEEN -> {
                    listener.addAll(listOf(Spawner()))
                }
            }
            if (event == Event.NONE) "Events wurden deaktiviert!".log(Color.RED)
            else "Event ${event.name} wurde aktiviert".log(Color.GREEN)
            true
        }
    }
}