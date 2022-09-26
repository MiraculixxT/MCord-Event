package de.miraculixx.mcord_event.utils.api

import de.miraculixx.mcord_event.config.ConfigManager
import de.miraculixx.mcord_event.config.Configs
import de.miraculixx.mcord_event.utils.log.Color
import de.miraculixx.mcord_event.utils.log.error
import de.miraculixx.mcord_event.utils.log.log
import kotlinx.coroutines.delay
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object SQL {
    private var connection: Connection

    private fun connect(): Connection {
        val con = DriverManager.getConnection(
            "jdbc:mariadb://localhost:3306/event_bot",
            "dcEventBot",
            ConfigManager.getConfig(Configs.CORE).getString("SQL_TOKEN")
        )
        if (con.isValid(0))
            ">> Connection established to MariaDB".log(Color.GREEN)
        else ">> ERROR > MariaDB refused the connection".error()
        return con
    }

    suspend fun call(statement: String, resultSet: Int? = null): ResultSet {
        while (!connection.isValid(1)) {
            "ERROR >> SQL - No valid connection!".error()
            connection = connect()
            delay(1000)
        }

        val query = if (resultSet != null) connection.prepareStatement(statement, resultSet)
        else connection.prepareStatement(statement)
        return query.executeQuery()
    }

    /*
    Interactions to the API
     */
    private suspend fun createUser(discordSnowflake: Long, twitchName: String?): UserData {
        // Generell User Account
        call("INSERT INTO accountConnect VALUES (default, $discordSnowflake, $twitchName)")
        val userData = call("SELECT ID FROM accountConnect WHERE Discord=$discordSnowflake")
        userData.next()
        val userID = userData.getInt("ID")

        // Create Empty Data Rows to simplify future calls
        call("INSERT INTO halloween22 VALUES ($userID, 0, 0, 0, 0, 0, 1, 0)")

        return UserData(userID, discordSnowflake, twitchName)
    }

    suspend fun getUser(discordSnowflake: Long): UserData {
        val result = call("SELECT * FROM accountConnect WHERE Discord=$discordSnowflake")
        if (!result.next()) return createUser(discordSnowflake, null)

        return UserData(
            result.getInt("ID"),
            result.getLong("Discord"),
            result.getString("Twitch")
        )
    }

    suspend fun getHalloweenData(discordSnowflake: Long): HalloweenData {
        val result = call("SELECT Points, C_Common, C_Rare, C_Epic, C_Legendary, N_Silver, N_Gold " +
                "FROM halloween22 JOIN accountConnect " +
                "WHERE Discord=$discordSnowflake && accountConnect.ID=halloween22.ID")
        result.next()

        return HalloweenData(
            result.getInt("Points"),
            result.getInt("C_Common"),
            result.getInt("C_Rare"),
            result.getInt("C_Epic"),
            result.getInt("C_Legendary"),
            result.getInt("N_Silver"),
            result.getInt("N_Gold")
        )
    }


    /**
     * @param id User ID
     * @param discordID Discord User Tag
     * @param twitchName Connected Twitch Account Name (nullable)
     */
    data class UserData(val id: Int, val discordID: Long, val twitchName: String?)

    /**
     * @param points Event Points
     * @param cCommon cRare, cEpic, cLegendary - Captured Creature Count
     * @param nSilver cGold - Available Nets
     */
    data class HalloweenData(val points: Int, val cCommon: Int, val cRare: Int, val cEpic: Int, val cLegendary: Int, val nSilver: Int, val nGold: Int)

    init {
        connection = connect()
    }
}