package com.pvp.app.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration
import java.time.LocalDateTime

object DurationSerializer : KSerializer<Duration?> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "java.time.Duration",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Duration?) {
        value?.let { encoder.encodeString(it.toString()) }
    }

    override fun deserialize(decoder: Decoder): Duration? {
        return try {
            Duration.parse(decoder.decodeString())
        } catch (e: Exception) {
            null
        }
    }
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "java.time.LocalDateTime",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}