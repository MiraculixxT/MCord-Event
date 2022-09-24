package de.miraculixx.mcord_event.modules.twitch

import com.github.philippheuer.events4j.simple.domain.EventSubscriber
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.miraculixx.mcord_event.utils.log.log

class ChatEvent {

    @EventSubscriber
    fun onMessageReceive(event: ChannelMessageEvent) {
        val message = event.message
        if (message.startsWith('!')) {
            //Command
            val args = message.split(' ')
            when (args[0]) {
                "!connect" -> {
                    val code = args.getOrNull(1)
                    if (code == null) event.reply(event.twitchChat, "/me Bitte gebe deinen Auth Code ein! Du erhältst einen vom Event Discord Bot über /connect")
                    //TODO("CHECK CODE AND VERIFY")
                    event.reply(event.twitchChat, "/me Deine Eingabe: $code")
                    "**TWITCH >>** User Code Input: `$code`".log()
                }
            }
        }
    }
}