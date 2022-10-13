package de.miraculixx.mcord_event.modules.events.shop

import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.entities.ButtonEvent
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class AccountInfo : ButtonEvent {
    override suspend fun trigger(it: ButtonInteractionEvent) {
        val member = it.member ?: return
        val memberID = member.idLong
        val userInfo = SQL.getHalloweenData(memberID)

        it.replyEmbeds(Embed {
            title = "Sammel Info"
            thumbnail = "https://pa1.narvii.com/7348/d7d55fd8fa7401b5a5ae50cb1c5e36a99c74f247r1-480-480_hq.gif"
            color = 0xd8761c
            description = "Deine Punkte und gesammelten Objekte...\n\n" +
                    "> **Punkte:** ${userInfo.points} <:Pumpkin:895271226304503858>\n" +
                    "> **Silber Netze:** ${userInfo.nSilver} <:silver_net:1023682513970925699>\n" +
                    "> **Gold Netze:** ${userInfo.nGold} <:gold_net:1023594135153807392> \n" +
                    "\n> **Common Monster:** ${userInfo.cCommon}\n" +
                    "> **Rare Monster:** ${userInfo.cRare}\n" +
                    "> **Epic Monster:** ${userInfo.cEpic}\n" +
                    "> **Legendary Monster:** ${userInfo.cLegendary}"
        }).setEphemeral(true).queue()
    }
}