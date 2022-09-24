package de.miraculixx.mcord_event.utils.entities

import dev.minn.jda.ktx.events.CoroutineEventListener


interface EventListener {
    val listener: CoroutineEventListener
    fun stopListen() {
        listener.cancel()
    }
}