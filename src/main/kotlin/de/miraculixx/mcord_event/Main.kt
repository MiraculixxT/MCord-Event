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
import de.miraculixx.mcord_event.utils.manager.BootUp
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

fun main() {
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

            // Discord Bot
            val dc = launch {
                val dcToken = coreConf.getString("DISCORD_TOKEN")
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
                consoleChannel = jda.getTextChannelById(1023336602992390186)!!

                "MKord is now online!".log(Color.GREEN)
            }

            // Twitch Bot
            val tv = launch {
                val token = OAuth2Credential("twitch", coreConf.getString("TWITCH_TOKEN"))
                twitchClient = TwitchClientBuilder.builder()
                    .withEnableHelix(true)
                    .withEnableChat(true)
                    .withChatAccount(token)
                    .withDefaultAuthToken(token)
                    .build()

                twitchClient.chat.joinChannel("miraculixxt")
                twitchClient.clientHelper.enableStreamEventListener("miraculixxt")

                "Twitch Client is now online!".log(Color.GREEN)
            }

            while (!tv.isCompleted ||  !dc.isCompleted) {} // await boot up
            BootUp(jda, twitchClient)
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
val TwitchClient by lazy { Main.twitchClient }