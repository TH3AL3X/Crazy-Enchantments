package me.badbones69.crazyenchantments.func.database

import com.google.gson.GsonBuilder
import me.badbones69.crazyenchantments.func.getFile
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.nio.charset.StandardCharsets

class JsonManager(private val plugin: JavaPlugin) {

    private val json = GsonBuilder()
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .excludeFieldsWithModifiers(128, 64).create()

    // Call this once on reloads / shutdown
    fun saveToFile(instance: Any, file: File) = write(DefaultFile(file, json.toJson(instance)))

    // Call this only on loads.
    fun <T> loadFromFile(default: T, classObject: Class<T>, file: File): T {

        fun <T> loadClass(classObject: Class<T>, file: File): T? {
            runCatching {
                val stream = FileInputStream(file)
                val reader = InputStreamReader(stream, StandardCharsets.UTF_8)
                return json.fromJson(reader, classObject)
            }
            return null
        }

        when {
            !file.exists() -> {
                return default
            }
            else -> {
                return loadClass(classObject, file) ?: return default
            }
        }
    }

    // Creates the directory & file name, This will run first.
    fun createDirectory(name: String) {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()

        val file = File("${plugin.dataFolder}/data", name)
        if (!file.exists()) file.createNewFile()
    }

    // This will write to the file using gson serialization

    private fun write(parent: DefaultFile) {
        val content = parent.contents
        val file = parent.file
        runCatching {
            val fileObject = File("${plugin.dataFolder}/data", file.name)
            val stream = FileOutputStream(fileObject)
            OutputStreamWriter(stream, StandardCharsets.UTF_8).use { it.write(content) }
        }
    }

    data class DefaultFile(val file: File, val contents: String)
}

class Data(private val plugin: JavaPlugin) {

    var testString = ""

    fun load() {
        JsonManager(plugin).createDirectory("data.json")

        JsonManager(plugin).loadFromFile(this, Data::class.java, getFile(plugin, "data.json"))
    }

    fun save() {
        JsonManager(plugin).saveToFile(this, getFile(plugin, "data.json"))
    }
}