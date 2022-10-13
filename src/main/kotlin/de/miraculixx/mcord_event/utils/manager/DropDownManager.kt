package de.miraculixx.mcord_event.utils.manager

import de.miraculixx.mcord_event.modules.events.shop.NetShop
import de.miraculixx.mcord_event.modules.events.shop.RoleShop
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent

object DropDownManager {
    private val dropDowns = mapOf(
        "SHOP:NETS" to NetShop(),
        "SHOP:ROLES" to RoleShop()
    )

    fun startListen(jda: JDA) = jda.listener<SelectMenuInteractionEvent> {
        val dropDownClass = dropDowns[it.componentId] ?: return@listener
        dropDownClass.trigger(it)
    }
}