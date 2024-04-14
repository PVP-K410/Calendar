package com.pvp.app.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException

object DurationSerializer : KSerializer<Duration?> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "java.time.Duration",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: Duration?
    ) {
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

object LocalDateSerializer : KSerializer<LocalDate> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "java.time.LocalDateTime",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: LocalDate
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val value = decoder.decodeString()

        return try {
            LocalDateTime
                .parse(value)
                .toLocalDate()
        } catch (e: DateTimeParseException) {
            LocalDate.parse(value)
        }
    }
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "java.time.LocalDateTime",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: LocalDateTime
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}

object LocalTimeSerializer : KSerializer<LocalTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "java.time.LocalTime",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: LocalTime
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString())
    }
}