package de.miraculixx.mcord_event.modules.events.shop

import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.entities.DropDownEvent
import de.miraculixx.mcord_event.utils.extensions.fancy
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent

class NetShop: DropDownEvent {
    override suspend fun trigger(it: SelectMenuInteractionEvent) {
        val selectedOption = it.selectedOptions.firstOrNull()?.value
        if (selectedOption == null) it.reply_("```fix\nDu hast nichts ausgewählt zum kaufen!```", ephemeral = true).queue()
        else {
            it.deferReply(true).queue()
            val args = selectedOption.split(':')
            val product = args.firstOrNull() ?: return
            val price = args.getOrNull(1)?.toIntOrNull() ?: return

            val userData = SQL.getHalloweenData(it.user.idLong)
            if (userData.points < price) {
                it.hook.editOriginal("```diff\n- Leider fehlen dir ${price - userData.points} Punkte :(\n- Jage weiter fleißig Monster um mehr zu erhalten!```").queue()
                return
            }
            val userID = SQL.getUser(it.user.idLong).id
            SQL.call("UPDATE halloween22 SET Points=Points-$price WHERE ID=$userID")
            var silverNets = userData.nSilver
            var goldNets = userData.nGold
            val call = "UPDATE halloween22 SET " + when (product) {
                "SILVER" -> {
                    silverNets+=1
                    "N_Silver=N_Silver+1"
                }
                "SILVER5" -> {
                    silverNets+=5
                    "N_Silver=N_Silver+5"
                }
                "GOLD" -> {
                    goldNets+=1
                    "N_Gold=N_Gold+1"
                }
                "GOLD5" -> {
                    goldNets+=5
                    "N_Gold=N_Gold+5"
                }
                else -> return
            } + " WHERE ID=$userID"
            SQL.call(call)

            it.hook.editOriginalEmbeds(Embed {
                title = "Fangnetz/e gekauft!"
                color = 0xd8761c
                description = "Du hast erfolgreich **${product.fancy()}** erworben\n" +
                        "\n> Punkte nach Kauf >> **${userData.points - price}** :candy:" +
                        "\n> Silber Netze >> **$silverNets** <:silver_net:1023682513970925699>" +
                        "\n> Gold Netze >> **$goldNets** <:gold_net:1023594135153807392>"
            }).queue()
        }
    }
}