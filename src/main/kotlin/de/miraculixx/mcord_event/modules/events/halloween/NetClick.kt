package de.miraculixx.mcord_event.modules.events.halloween

import de.miraculixx.mcord_event.JDA
import de.miraculixx.mcord_event.modules.events.EventManager
import de.miraculixx.mcord_event.modules.events.halloween.data.CatchNet
import de.miraculixx.mcord_event.utils.entities.EventListener
import de.miraculixx.mcord_event.utils.enumOf
import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class NetClick: EventListener {
    override val listener: CoroutineEventListener = JDA.listener<ButtonInteractionEvent> {
        val id = it.componentId
        val args = id.split(':')
        if (args.getOrNull(0) != "EVENT") return@listener
        if (args.getOrNull(1) != "HALLOWEEN") return@listener
        val net = enumOf<CatchNet>(args.getOrNull(2)) ?: return@listener

        it.deferReply(true).queue()
        EventManager.currentCreature?.useCatchNet(net, it.hook)
    }
}