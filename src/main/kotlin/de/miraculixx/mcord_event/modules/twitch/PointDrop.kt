package de.miraculixx.mcord_event.modules.twitch

import com.github.philippheuer.events4j.api.domain.IEventSubscription
import com.github.philippheuer.events4j.core.EventManager
import com.github.twitch4j.events.ChannelGoLiveEvent
import com.github.twitch4j.events.ChannelGoOfflineEvent
import de.miraculixx.mcord_event.TwitchClient
import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.log.Color
import de.miraculixx.mcord_event.utils.log.log
import dev.minn.jda.ktx.events.getDefaultScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class PointDrop {
    private var isOnline = false

    fun onGoOnline(eventManager: EventManager): IEventSubscription = eventManager.onEvent(ChannelGoLiveEvent::class.java) {
        "**TWITCH** >> Miraculixx is now online".log(Color.GREEN)
        isOnline = true
    }

    fun onGoOffline(eventManager: EventManager): IEventSubscription = eventManager.onEvent(ChannelGoOfflineEvent::class.java) {
        "**TWITCH** >> Miraculixx is now offline".log(Color.RED)
        isOnline = false
    }

    private fun checker() = getDefaultScope().launch {
        if (isOnline) {
            val chatters = TwitchClient.messagingInterface.getChatters("miraculixxt").execute()
            "**Twitch** >> ${chatters.allViewers.size} Chatters getting 5 Points".log()
            chatters.allViewers.forEach { viewer ->
                val response = SQL.call("SELECT ID FROM accountConnect WHERE Twitch=$viewer")
                if (response.next()) {
                    val id = response.getInt("ID")
                    SQL.call("UPDATE halloween22 SET Points=Points+5 WHERE ID=$id")
                }
            }
        }

        delay(15.minutes)
    }

    init {
        checker()
    }
}