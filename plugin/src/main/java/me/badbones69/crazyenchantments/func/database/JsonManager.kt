package me.badbones69.crazyenchantments.func.database

import com.google.gson.GsonBuilder
import me.corecraft.func.database.json.adapters.LocalTimeTypeAdapter
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.time.LocalTime

class JsonManager(private val plugin: JavaPlugin) {

    private val json = GsonBuilder()
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .excludeFieldsWithModifiers(128, 64)
        .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY).create()

    // Call this once on reloads / shutdown
    fun saveToFile(instance: Any, name: String) {
        val file = File(name)

        val broken = File("${file.toPath()}.back-up")
        if (file.exists()) {
            runCatching {
                if (broken.exists()) broken.delete()
                Files.copy(file.toPath(), broken.toPath())
            }.onFailure { plugin.logger.info(it.message) }
        }

        broken.delete()
        return write(DefaultFile(file, json.toJson(instance)))
    }

    // Call this only on loads.
    fun <T> loadFromFile(default: T, classObject: Class<T>, name: String): T {
        val file = File(name)

        fun <T> loadClass(classObject: Class<T>): T? {
            if (file.exists()) {
                runCatching {
                    val stream = FileInputStream(file)
                    val reader = InputStreamReader(stream, StandardCharsets.UTF_8)
                    return json.fromJson(reader, classObject)
                }
            }
            return null
        }

        fun createBackUp(): T {
            val backup = File("${file.path}_bad")
            if (backup.exists()) backup.delete()
            file.renameTo(backup)
            return default
        }

        when {
            !file.exists() -> {
                saveToFile(default!!, name)
                return default
            }
            else -> {
                return loadClass(classObject) ?: return createBackUp()
            }
        }
    }

    // Creates the directory & file name, This will run first.
    fun createDirectory(name: String) {

        val path = File(plugin.dataFolder, "/data")

        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
        if (!path.exists()) path.mkdirs()

        val file = File(path, name)
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
        }.onFailure { plugin.logger.info(it.message) }
    }

    data class DefaultFile(val file: File, val contents: String)
}

class Data(private val plugin: JavaPlugin) {

    var testString = ""

    //fun load() = Serializer(plugin.dataFolder, true).load(this, Data::class.java, "config")

    fun load() {
        JsonManager(plugin).createDirectory("data.json")

        JsonManager(plugin).loadFromFile(this, Data::class.java, "config")
    }

    fun save() {
        JsonManager(plugin).saveToFile(this, "config")
    }

    //fun save() = Serializer(plugin.dataFolder, true).save(this, "config")
}