package de.miraculixx.mcord_event.config

import de.miraculixx.mcord_event.Main
import java.io.File

object ConfigManager {
    private val configs: Map<Configs, Config>

    fun getConfig(type: Configs): Config {
        return configs[type] ?: configs[Configs.CORE]!!
    }

    //static values
    val apiKey: String

    init {
        var jarPath = File(this.javaClass.protectionDomain.codeSource.location.toURI()).path
        val jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1)
        jarPath = jarName.removeSuffix(jarName)
        val cl = Main::class.java

        val s = File.separator
        val configFolder = File("$jarPath${s}config")
        if (!configFolder.exists() || !configFolder.isDirectory) configFolder.mkdirs()

        configs = mapOf(
            Configs.CORE to Config(cl.getResourceAsStream("core.yml"), "core"),
            Configs.SETTINGS to Config(cl.getResourceAsStream("config.yml"), "config"),
        )


        //Applying values
        apiKey = getConfig(Configs.CORE).getString("API_TOKEN")
    }
}