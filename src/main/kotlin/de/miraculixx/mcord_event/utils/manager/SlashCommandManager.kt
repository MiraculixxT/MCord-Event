package de.miraculixx.mcord_event.utils.manager

import de.miraculixx.mcord_event.JDA
import de.miraculixx.mcord_event.modules.commands.ToggleEventCMD
import de.miraculixx.mcord_event.utils.log.log
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.commands.Command
import dev.minn.jda.ktx.interactions.commands.choice
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.updateCommands
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

object SlashCommandManager {
    private val commands = mapOf(
        "change-event" to ToggleEventCMD()
    )

    fun startListen(jda: JDA) = jda.listener<SlashCommandInteractionEvent> {
        val commandClass = commands[it.name] ?: return@listener
        val options = buildList { it.options.forEach { option -> add(option.asString + " ") } }
        ">> ${it.user.asTag} -> /${it.name}${it.subcommandName ?: ""} $options".log()
        commandClass.trigger(it)
    }

    init {
        //Implement all Commands into Discord

        val guilds = listOf(989881712492298250).map { JDA.getGuildById(it) }
        guilds.forEach {
            it?.updateCommands {
                Command("change-event", "Change the current active event (none = disable)") {
                    option<String>("event", "The new Event (none = disable)", required = true) {
                        choice("NONE", "None")
                        choice("HALLOWEEN", "Halloween")
                    }
                }
                Command("leaderboard", "Zeige das aktuelle Leaderboard der Event Punkte Liste an") {
                    option<Boolean>("all", "Nicht nur Top 10 (admin only)")
                }
                Command("connect", "Verbinde deinen Discord Account mit deinem Twitch Account zum punkten!") {
                    option<String>("twitch-name", "Dein Twitch Account Name", true)
                }
            }?.queue()
        }
    }
}