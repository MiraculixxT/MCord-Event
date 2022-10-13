package de.miraculixx.mcord_event.utils.manager

import de.miraculixx.mcord_event.modules.commands.SendCMD
import de.miraculixx.mcord_event.modules.connect.ConnectButton
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object ModalManager {
    private val modals = mapOf(
        "SEND:CONTENT" to SendCMD(),
        "INFO:CONNECT" to ConnectButton()
    )

    fun startListen(jda: JDA) = jda.listener<ModalInteractionEvent> {
        val modalClass = modals[it.modalId] ?: return@listener
        modalClass.trigger(it)
    }
}

