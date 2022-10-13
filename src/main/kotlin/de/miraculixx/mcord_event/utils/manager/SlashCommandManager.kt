package de.miraculixx.mcord_event.utils.manager

import de.miraculixx.mcord_event.JDA
import de.miraculixx.mcord_event.modules.commands.*
import de.miraculixx.mcord_event.modules.events.halloween.data.CreatureType
import de.miraculixx.mcord_event.utils.guildTest
import de.miraculixx.mcord_event.utils.log.log
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.commands.Command
import dev.minn.jda.ktx.interactions.commands.choice
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.upsertCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

object SlashCommandManager {
    private val commands = mapOf(
        "change-event" to ToggleEventCMD(),
        "connect" to ConnectAccountCMD(),
        "leaderboard" to LeaderboardCMD(),
        "send" to SendCMD(),
        "admin" to AdminCMD()
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
        JDA.updateCommands().addCommands(
            Command("send", "Sends any kind of message") {
                defaultPermissions = DefaultMemberPermissions.DISABLED
                option<String>("msg", "The message content to send")
            },
            Command("admin", "Admin Command") {
                defaultPermissions = DefaultMemberPermissions.DISABLED
                option<String>("call", "The Action", true) {
                    choice("Net-Shop", "NET_SHOP")
                    choice("Role-Shop", "ROLE_SHOP")
                    choice("Remove-Components", "REMOVE_COMPS")
                    choice("Get-Points", "GET_POINTS")
                    choice("Add-Points", "ADD_POINTS")
                    choice("Spawn", "SPAWN")
                    choice("Delete", "DELETE")
                    choice("Info-Buttons", "INFO")
                }
                option<String>("id", "Any Component ID")
                option<Int>("number", "Any amount or number")
                option<String>("type", "Enum Type") {
                    CreatureType.values().forEach {
                        choice(it.name, it.name)
                    }
                }
            },
            Command("connect", "Verbinde deinen Discord Account mit deinem Twitch Account zum punkten!") {
                isGuildOnly = true
                option<String>("twitch-name", "Dein Twitch Account Name", true)
            }
        ).queue()

        guildTest.upsertCommand("change-event", "Change the current active event (none = disable)") {
            defaultPermissions = DefaultMemberPermissions.DISABLED
            option<String>("event", "The new Event (none = disable)", true) {
                choice("None", "NONE")
                choice("Halloween", "HALLOWEEN")
            }
        }.queue()
        guildTest.upsertCommand("leaderboard", "Zeige das aktuelle Leaderboard der Event Punkte Liste an") {
            option<Boolean>("all", "Nicht nur Top 10 (admin only)")
        }.queue()
    }
}