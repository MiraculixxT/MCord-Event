package de.miraculixx.mcord_event.modules.events.halloween

import de.miraculixx.mcord_event.JDA
import de.miraculixx.mcord_event.modules.events.Event
import de.miraculixx.mcord_event.modules.events.EventManager
import de.miraculixx.mcord_event.modules.events.halloween.data.CreatureType
import de.miraculixx.mcord_event.modules.events.halloween.data.Rarity
import de.miraculixx.mcord_event.utils.entities.EventListener
import de.miraculixx.mcord_event.utils.log.Color
import de.miraculixx.mcord_event.utils.log.log
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Spawner : EventListener {
    private var leftToSpawn = 0

    override val listener = JDA.listener<MessageReceivedEvent> {
        if (it.author.isBot || it.author.isSystem) return@listener
        if (EventManager.getEvent() != Event.HALLOWEEN) return@listener
        leftToSpawn--
        if (EventManager.currentCreature != null) return@listener
        if (leftToSpawn <= 0) {

            spawnCreature(it.channel)

            leftToSpawn = (2..3).random()
            "**HALLOWEEN >>** Spawn a new creature".log(Color.GREEN)
        }
        "**HALLOWEEN >>** Left to spawn: $leftToSpawn".log()
    }

    private fun spawnCreature(channel: MessageChannel) {
        val rarity = when ((0..100).random()) {
            in 0..8 -> Rarity.LEGENDARY // 9%
            in 9..25 -> Rarity.EPIC // 17%
            in 26..54 -> Rarity.RARE // 29%
            else -> Rarity.COMMON // 45%
        }

        val type = CreatureType.values().filter { it.rarity == rarity }.random()
        val creature = Creature(type)
        creature.spawn(channel.jda)
        EventManager.currentCreature = creature
    }
}