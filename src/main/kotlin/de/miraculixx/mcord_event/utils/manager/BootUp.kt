package de.miraculixx.mcord_event.utils.manager

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.TwitchClient
import de.miraculixx.mcord_event.config.ConfigManager
import de.miraculixx.mcord_event.config.Configs
import de.miraculixx.mcord_event.modules.events.Event
import de.miraculixx.mcord_event.modules.events.EventManager
import de.miraculixx.mcord_event.modules.twitch.ChatEvent
import net.dv8tion.jda.api.JDA

class BootUp(jda: JDA, twitchClient: TwitchClient) {

    init {
        loadEventData()
        registerEvents(jda, twitchClient)
    }

    private fun loadEventData() {
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        val autostart = config.getEnum("default-event") ?: Event.NONE
        EventManager.activateEvent(autostart)
    }

    private fun registerEvents(jda: JDA, twitchClient: TwitchClient) {
        SlashCommandManager.startListen(jda)

        val eventManager = twitchClient.eventManager
        eventManager.getEventHandler(SimpleEventHandler::class.java).registerListener(ChatEvent())
    }
}