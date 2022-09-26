package de.miraculixx.mcord_event.modules.connect

data class UserAuthentication(val discordSnowflake: Long, val twitchName: String, val code: String)