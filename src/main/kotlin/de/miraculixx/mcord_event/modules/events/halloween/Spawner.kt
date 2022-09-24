package de.miraculixx.mcord_event.modules.events.halloween

import de.miraculixx.mcord_event.JDA
import de.miraculixx.mcord_event.modules.events.Event
import de.miraculixx.mcord_event.utils.entities.EventListener
import de.miraculixx.mcord_event.modules.events.EventManager
import de.miraculixx.mcord_event.utils.log.Color
import de.miraculixx.mcord_event.utils.log.log
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Spawner: EventListener {
    private var leftToSpawn = 0

    override val listener = JDA.listener<MessageReceivedEvent> {
        if (EventManager.getEvent() != Event.HALLOWEEN) return@listener
        if (it.author.isBot || it.author.isSystem) return@listener
        leftToSpawn--
        if (leftToSpawn <= 0) {
            //Spawn Creature
            leftToSpawn = (30..80).random()
            "**HALLOWEEN >>** Spawn a new creature".log(Color.GREEN)
        }
        "**HALLOWEEN >>** Left to spawn: $leftToSpawn".log()
    }
}