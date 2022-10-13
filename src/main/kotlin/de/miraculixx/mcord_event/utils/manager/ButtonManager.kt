package de.miraculixx.mcord_event.utils.manager

import de.miraculixx.mcord_event.modules.connect.ConnectButton
import de.miraculixx.mcord_event.modules.events.shop.AccountInfo
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object ButtonManager {
    private val buttons = mapOf(
        "INFO:TWITCH" to ConnectButton(),
        "INFO:USER" to AccountInfo(),
    )

    fun startListen(jda: JDA) = jda.listener<ButtonInteractionEvent> {
        val buttonClass = buttons[it.componentId] ?: return@listener
        buttonClass.trigger(it)
    }
}