package de.miraculixx.mcord_event

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import de.miraculixx.mcord_event.config.ConfigManager
import de.miraculixx.mcord_event.config.Configs
import de.miraculixx.mcord_event.modules.twitch.ChatEvent
import de.miraculixx.mcord_event.utils.log.Color
import de.miraculixx.mcord_event.utils.log.consoleChannel
import de.miraculixx.mcord_event.utils.log.log
import de.miraculixx.mcord_event.utils.manager.SlashCommandManager
import dev.minn.jda.ktx.events.getDefaultScope
import dev.minn.jda.ktx.jdabuilder.default
import dev.minn.jda.ktx.jdabuilder.intents
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.util.*

fun main(args: Array<String>) {
    Main()
}

class Main {
    companion object {
        lateinit var jda: JDA
        lateinit var twitchClient: TwitchClient
    }

    init {
        getDefaultScope().launch {
            val coreConf = ConfigManager.getConfig(Configs.CORE)
            val dcToken = coreConf.getString("DISCORD_TOKEN")
            dcToken.log()
            jda = default(dcToken) {
                disableCache(CacheFlag.VOICE_STATE)
                setActivity(Activity.watching("spooky creatures"))
                setStatus(OnlineStatus.DO_NOT_DISTURB)
                intents += listOf(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                intents -= listOf(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGE_TYPING)
                setMemberCachePolicy(MemberCachePolicy.ALL)
                setChunkingFilter(ChunkingFilter.include(707925156919771158))
            }
            jda.awaitReady()

            SlashCommandManager.startListen(jda)

            consoleChannel = jda.getGuildById(989881712492298250)?.getTextChannelById(1021752087803076698)

            "MKord is now online!".log(Color.GREEN)

            // Twitch Bot
            val token = OAuth2Credential("twitch", coreConf.getString("TWITCH_TOKEN"))
            twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withChatAccount(token)
                .withDefaultAuthToken(token)
                .build()

            twitchClient.chat.joinChannel("miraculixxt")
            twitchClient.clientHelper.enableStreamEventListener("miraculixxt")

            val eventManager = twitchClient.eventManager
            eventManager.getEventHandler(SimpleEventHandler::class.java).registerListener(ChatEvent())

            "Twitch Client is now online!".log(Color.GREEN)
        }

        shutdown()
    }

    private fun shutdown() {
        runBlocking {
            var online = true
            while (online) {
                when (val out = readLine() ?: continue) {
                    "exit" -> {
                        jda.shardManager?.setStatus(OnlineStatus.OFFLINE)
                        jda.shutdown()
                        println("MKord-Event is now offline!")
                        online = false
                    }

                    else -> {
                        println("Command $out not found!\nCurrent Commands -> 'exit'")
                    }
                }
            }
        }
    }
}

val JDA by lazy { Main.jda }