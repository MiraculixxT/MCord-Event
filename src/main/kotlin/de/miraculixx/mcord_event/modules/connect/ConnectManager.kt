package de.miraculixx.mcord_event.modules.connect

import de.miraculixx.mcord_event.utils.api.SQL
import kotlin.collections.HashMap

object ConnectManager {
    // <code>: <userData>
    private val codeMap = HashMap<String, UserAuthentication>()

    fun getCode(discordSnowflake: Long): String? {
        return codeMap.filter { it.value.discordSnowflake == discordSnowflake }.keys.firstOrNull()
    }

    fun hasCode(discordSnowflake: Long): Boolean {
        return getCode(discordSnowflake) != null
    }

    fun getAuthenticationUser(code: String): UserAuthentication? {
        return codeMap[code]
    }

    suspend fun authenticateUser(user: UserAuthentication) {
        codeMap.remove(user.code)
        SQL.call("UPDATE accountConnect SET Twitch='${user.twitchName}' WHERE Discord=${user.discordSnowflake}")
    }

    fun generateCode(discordSnowflake: Long, twitchName: String): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val code = (1..6)
            .map { allowedChars.random() }
            .joinToString("")
        codeMap[code] = UserAuthentication(discordSnowflake, twitchName, code)
        return code
    }

    suspend fun isConnected(discordSnowflake: Long): Boolean {
        val user = SQL.getUser(discordSnowflake)
        return user.twitchName != null
    }
}