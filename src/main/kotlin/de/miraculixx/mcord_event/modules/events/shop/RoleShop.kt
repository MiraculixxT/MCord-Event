package de.miraculixx.mcord_event.modules.events.shop

import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.entities.DropDownEvent
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import java.time.Instant

class RoleShop: DropDownEvent {
    override suspend fun trigger(it: SelectMenuInteractionEvent) {
        val selectedOption = it.selectedOptions.firstOrNull()?.value
        if (selectedOption == null) it.reply_("```fix\nDu hast nichts ausgewählt zum kaufen!```", ephemeral = true).queue()
        else {
            it.deferReply(true).queue()
            val args = selectedOption.split(':')
            val product = args.firstOrNull() ?: return
            val price = args.getOrNull(1)?.toIntOrNull() ?: return

            val snowflake = it.user.idLong
            val userID = SQL.getUser(snowflake).id
            val userData = SQL.getHalloweenData(snowflake)
            if (userData.points < price) {
                it.hook.editOriginal("```diff\n- Leider fehlen dir ${price - userData.points} Punkte :(\n- Jage weiter fleißig Monster um mehr zu erhalten!```").queue()
                return
            }
            SQL.call("UPDATE halloween22 SET Points=Points-$price WHERE ID=$userID")

            val roleData: Pair<Long, Long> = when (product) {
                "COLOR" -> 1025326501584961546 to 604800000
                "COLOR-M" -> 1025326501584961546 to 2592000000
                "LISTING" -> 1025327611938873354 to 604800000
                "LISTING-M" -> 1025327611938873354 to 2592000000
                else -> return
            }

            val role = it.guild?.getRoleById(roleData.first) ?: return
            val decayIn = Instant.now().toEpochMilli() + roleData.second
            SQL.call("INSERT INTO tempRoles VALUES ($userID, ${roleData.first}, $decayIn, '$product')")
            it.guild?.addRoleToMember(UserSnowflake.fromId(snowflake), role)?.queue()
            it.hook.editOriginalEmbeds(Embed {
                title = "Cosmetic gekauft!"
                color = 0xd8761c
                description = "Du hast erfolgreich eine Cosmetic Role erworben\n" +
                        "\n> Punkte nach Kauf >> **${userData.points - price}** :candy:" +
                        "\n> Neue Rolle >> ${role.asMention}" +
                        "\n> Läuft aus <t:${decayIn / 1000}:R>"
            }).queue()
        }
    }
}