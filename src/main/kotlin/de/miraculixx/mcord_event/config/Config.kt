@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package de.miraculixx.mcord_event.config

import de.miraculixx.mcord_event.utils.log.log
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream
import kotlin.io.path.Path

class Config(stream: InputStream?, private val name: String) {
    private val yaml: Yaml = Yaml()
    private val configMap: Map<String, Any>


    fun getString(name: String): String {
        return configMap[name].toString()
    }

    fun getStringList(name: String): List<String> {
        return try {
            configMap[name] as List<String>
        } catch (e: ClassCastException) {
            "ERROR > Value $name in Config ${this.name} cannot be casted to List<String>".log()
            emptyList()
        } catch (e: NullPointerException) {
            "ERROR > Value $name in Config ${this.name} is empty".log()
            emptyList()
        }
    }

    fun <T> getObjectList(name: String): LinkedHashMap<String, T> {
        return try {
            val origin = configMap[name] as List<Map<String, T>>
            val map = LinkedHashMap<String, T>()
            origin.forEach {
                val data = it.entries.first()
                map[data.key] = data.value
            }
            map
        } catch (e: ClassCastException) {
            "ERROR > Value $name in Config ${this.name} cannot be casted to List<Map<String, T>>".log()
            linkedMapOf()
        } catch (e: NullPointerException) {
            "ERROR > Value $name in Config ${this.name} is empty".log()
            linkedMapOf()
        }
    }

    fun getInt(name: String): Int {
        return getString(name).toIntOrNull() ?: 0
    }

    fun getLong(name: String): Long {
        return getString(name).toLongOrNull() ?: 0
    }

    fun getBoolean(name: String): Boolean {
        return getString(name).lowercase() == "true"
    }


    private fun loadConfig(file: File) {
        ">> Create new Config File - $name".log()
        val classLoader = this.javaClass.classLoader
        if (!file.exists()) {
            file.createNewFile()
            val stream = classLoader.getResourceAsStream(name)
            if (stream != null) file.writeBytes(stream.readAllBytes())
        }
    }

    init {
        ">> Load Config - $name".log()
        val file = Path("config/$name.yml").toFile()
        configMap = if (stream != null) {
            if (!file.exists()) loadConfig(file)

            try {
                yaml.load(file.inputStream())
            } catch (e: Exception) {
                e.printStackTrace()
                "ERROR - Failed to load Configuration File. ^^ Reason above ^^".log()
                "ERROR - Config Path -> ${file.path}".log()
                emptyMap()
            }
        } else {
            "ERROR - Configuration file is null".log()
            emptyMap()
        }
    }
}