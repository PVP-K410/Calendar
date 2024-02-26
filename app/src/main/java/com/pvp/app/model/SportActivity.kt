package com.pvp.app.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(SportActivity.Serializer::class)
sealed class SportActivity(
    val supportsDistanceMetrics: Boolean,
    val title: String
) {

    companion object {
        fun fromTitle(title: String): SportActivity? {
            return when (title) {
                Cycling.title -> Cycling
                Gym.title -> Gym
                Running.title -> Running
                Swimming.title -> Swimming
                Walking.title -> Walking
                Yoga.title -> Yoga
                else -> null
            }
        }
    }

    data object Cycling : SportActivity(true, "Cycling")
    data object Gym : SportActivity(false, "Gym")
    data object Running : SportActivity(true, "Running")
    data object Swimming : SportActivity(true, "Swimming")
    data object Walking : SportActivity(true, "Walking")
    data object Yoga : SportActivity(false, "Yoga")

    object Serializer : KSerializer<SportActivity> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            "com.pvp.app.model.SportActivity",
            PrimitiveKind.STRING
        )

        override fun deserialize(decoder: Decoder): SportActivity {
            return fromTitle(decoder.decodeString()) ?: error("Unknown sport activity")
        }

        override fun serialize(encoder: Encoder, value: SportActivity) {
            encoder.encodeString(value.title)
        }
    }
}
