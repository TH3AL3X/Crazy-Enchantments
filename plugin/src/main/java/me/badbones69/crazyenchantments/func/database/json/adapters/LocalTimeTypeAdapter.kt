package me.corecraft.func.database.json.adapters

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalTime

class LocalTimeTypeAdapter : JsonSerializer<LocalTime?>, JsonDeserializer<LocalTime?> {

    override fun serialize(localTime: LocalTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()
        runCatching {
            jsonObject.add("hour", JsonPrimitive(localTime!!.hour))
            jsonObject.add("minute", JsonPrimitive(localTime.minute))
            jsonObject.add("second", JsonPrimitive(localTime.second))
            jsonObject.add("nano", JsonPrimitive(localTime.nano))
            jsonObject
        }.onFailure { println(it.message) }
        return jsonObject
    }

    override fun deserialize(jsonElement: JsonElement, type: Type?, jsonDeserializationContext: JsonDeserializationContext?): LocalTime {
        val jsonObject: JsonObject = jsonElement.asJsonObject
        runCatching {
            LocalTime.of(jsonObject.get("hour").asInt, jsonObject.get("minute").asInt, jsonObject.get("second").asInt, jsonObject.get("nano").asInt)
        }.onFailure { println(it.message) }
        return LocalTime.now()
    }
}