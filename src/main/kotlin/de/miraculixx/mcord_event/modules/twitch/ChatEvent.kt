package de.miraculixx.mcord_event.modules.twitch

import com.github.philippheuer.events4j.simple.domain.EventSubscriber
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.miraculixx.mcord_event.modules.connect.ConnectManager
import de.miraculixx.mcord_event.utils.log.log
import dev.minn.jda.ktx.events.getDefaultScope
import kotlinx.coroutines.launch

class ChatEvent {

    @EventSubscriber
    fun onMessageReceive(event: ChannelMessageEvent) = getDefaultScope().launch {
        val message = event.message
        if (message.startsWith('!')) {
            //Command
            val args = message.split(' ')
            when (args[0]) {
                "!connect" -> {
                    val code = args.getOrNull(1)
                    if (code == null) {
                        event.reply(event.twitchChat, "/me Bitte gebe deinen Auth Code ein! Du erhältst einen vom Event Discord Bot über /connect")
                        return@launch
                    }

                    val auth = ConnectManager.getAuthenticationUser(code)
                    if (auth == null) {
                        event.reply(event.twitchChat, "/me Der angegebene Code '$code' existiert nicht!")
                        return@launch
                    }
                    if (event.user.name.equals(auth.twitchName, true)) {
                        ConnectManager.authenticateUser(auth)
                        event.reply(event.twitchChat, "/me Glückwunsch! Dein Account ist jetzt verbunden")

                    } else event.reply(event.twitchChat, "/me Der angegebene Code '$code' existiert nicht!")

                    "**TWITCH >>** User Code Input: `$code`".log()
                }
            }
        }
    }
}