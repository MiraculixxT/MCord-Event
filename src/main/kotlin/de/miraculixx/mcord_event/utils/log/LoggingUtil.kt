@file:Suppress("unused")

package de.miraculixx.mcord_event.utils.log

import dev.minn.jda.ktx.events.getDefaultScope
import dev.minn.jda.ktx.messages.send
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import java.time.Instant
import java.util.logging.Logger

private val logger = Logger.getGlobal()
var consoleChannel: MessageChannel? = null

fun String.log(color: Color = Color.WHITE) {
    printToConsole(this, "\u001B[${color.code}m")
}

fun String.error() {
    printToConsole(this, "\u001b[${Color.RED.code}m")
}

private fun printToConsole(input: String, color: String) = getDefaultScope().launch {
    logger.info(color + input)
    val timestamp = Instant.now().epochSecond
    consoleChannel?.send("<t:$timestamp:T> $input")
}

private fun prettyNumber(int: Int): String {
    return if (int <= 9) "0$int" else int.toString()
}

enum class Color(val code: Byte) {
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    GRAY(90),
    WHITE(97)
}