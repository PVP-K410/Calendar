package com.pvp.app.common.util

import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object JsonUtil {

    /**
     * JSON serializer with polymorphic support for [Task] subclasses
     */
    val JSON = Json {
        serializersModule = SerializersModule {
            polymorphic(
                Task::class,
                Task.serializer()
            ) {
                subclass(
                    MealTask::class,
                    MealTask.serializer()
                )
                subclass(
                    SportTask::class,
                    SportTask.serializer()
                )
            }
        }
    }

    /**
     * Converts any object to [JsonElement]
     */
    fun Any?.toJsonElement(): JsonElement = when (this) {
        null -> JsonNull
        is Map<*, *> -> toJsonElement()
        is Collection<*> -> toJsonElement()
        is ByteArray -> toList().toJsonElement()
        is CharArray -> toList().toJsonElement()
        is ShortArray -> toList().toJsonElement()
        is IntArray -> toList().toJsonElement()
        is LongArray -> toList().toJsonElement()
        is FloatArray -> toList().toJsonElement()
        is DoubleArray -> toList().toJsonElement()
        is BooleanArray -> toList().toJsonElement()
        is Array<*> -> toJsonElement()
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Enum<*> -> JsonPrimitive(toString())

        else -> {
            error("Can't serialize unknown type: $this")
        }
    }

    /**
     * Converts [Map] to [JsonObject]
     */
    fun Map<*, *>.toJsonElement(): JsonElement {
        val map = mutableMapOf<String, JsonElement>()

        forEach { (key, value) ->
            map[key as String] = value.toJsonElement()
        }

        return JsonObject(map)
    }

    /**
     * Converts [Collection] to [JsonArray]
     */
    fun Collection<*>.toJsonElement(): JsonElement {
        return JsonArray(this.map { it.toJsonElement() })
    }

    /**
     * Converts [Array] to [JsonArray]
     */
    fun Array<*>.toJsonElement(): JsonElement {
        return JsonArray(this.map { it.toJsonElement() })
    }

    /**
     * Converts [JsonElement] to a map of primitives. Only supports [JsonObject]
     */
    fun JsonElement.toPrimitivesMap(): Map<String, Any?> {
        return when (this) {
            is JsonObject -> {
                val values = mutableMapOf<String, Any?>()

                forEach { (key, value) ->
                    when (value) {
                        is JsonPrimitive -> value.jsonPrimitive.contentOrNull?.let {
                            values[key] = it
                        }

                        is JsonObject -> values[key] = value.toPrimitivesMap()
                        is JsonArray -> values[key] = value.toPrimitivesList()

                        else -> {}
                    }
                }

                values
            }

            else -> error("Can't convert $this to Map")
        }
    }

    /**
     * Converts [JsonElement] to a list of primitives. Only supports [JsonArray]
     */
    fun JsonElement.toPrimitivesList(): List<Any?> {
        return when (this) {
            is JsonArray -> mapNotNull {
                when (it) {
                    is JsonPrimitive -> it.jsonPrimitive.contentOrNull
                    is JsonObject -> it.toPrimitivesMap()
                    is JsonArray -> it.toPrimitivesList()
                    else -> null
                }
            }

            else -> error("Can't convert $this to List")
        }
    }
}