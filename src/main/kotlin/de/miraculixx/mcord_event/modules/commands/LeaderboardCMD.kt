package de.miraculixx.mcord_event.modules.commands

import de.miraculixx.mcord_event.utils.api.SQL
import de.miraculixx.mcord_event.utils.entities.SlashCommandEvent
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class LeaderboardCMD: SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        val member = it.member
        val jda = it.jda
        val top10 = if (it.member?.idLong == 341998118574751745) it.getOption("all")?.asBoolean ?: false else false
        it.deferReply()

        val result = SQL.call("SELECT * FROM halloween22 JOIN accountConnect ${if (top10) "LIMIT 10" else ""}")
        it.hook.editOriginalEmbeds(Embed {
            title = "**Punkte Leaderboard**"
            description = buildString {
                append("<:blanc:784059217890770964>")
                var counter = 1
                while (result.next()) {
                    val id = result.getLong("Discord")
                    val user = jda.getUserById(id) ?: jda.retrieveUserById(id).complete()
                    append("\n> `Platz $counter` >> ${user.asTag} - **${result.getInt("Points")}**")
                    counter++
                }
            }
        }).queue()
    }
}