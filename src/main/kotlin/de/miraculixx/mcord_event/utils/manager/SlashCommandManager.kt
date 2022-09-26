package de.miraculixx.mcord_event.utils.manager

import de.miraculixx.mcord_event.JDA
import de.miraculixx.mcord_event.modules.commands.ConnectAccountCMD
import de.miraculixx.mcord_event.modules.commands.LeaderboardCMD
import de.miraculixx.mcord_event.modules.commands.ToggleEventCMD
import de.miraculixx.mcord_event.utils.guildMiraculixx
import de.miraculixx.mcord_event.utils.guildTest
import de.miraculixx.mcord_event.utils.log.log
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.commands.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

object SlashCommandManager {
    private val commands = mapOf(
        "change-event" to ToggleEventCMD(),
        "connect" to ConnectAccountCMD(),
        "leaderboard" to LeaderboardCMD()
    )

    fun startListen(jda: JDA) = jda.listener<SlashCommandInteractionEvent> {
        val commandClass = commands[it.name] ?: return@listener
        val options = buildString { it.options.forEach { option -> append(option.asString + " ") } }
        ">> ${it.user.asTag} -> /${it.name}${it.subcommandName ?: ""} $options".log()
        commandClass.trigger(it)
    }

    init {
        //Implement all Commands into Discord

        "Guilds - ${JDA.guilds.size}".log()
        listOf(guildTest, guildMiraculixx).forEach {
            it.upsertCommand("change-event", "Change the current active event (none = disable)") {
                defaultPermissions = DefaultMemberPermissions.DISABLED
                option<String>("event", "The new Event (none = disable)", true) {
                    choice("None", "NONE")
                    choice("Halloween", "HALLOWEEN")
                }
            }.queue()
            it.upsertCommand("leaderboard", "Zeige das aktuelle Leaderboard der Event Punkte Liste an") {
                option<Boolean>("all", "Nicht nur Top 10 (admin only)")
            }.queue()
            it.upsertCommand("connect", "Verbinde deinen Discord Account mit deinem Twitch Account zum punkten!") {
                isGuildOnly = true
                option<String>("twitch-name", "Dein Twitch Account Name", true)
            }.queue()
        }
    }
}