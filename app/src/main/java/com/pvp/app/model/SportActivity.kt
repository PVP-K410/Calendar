package com.pvp.app.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(SportActivity.Companion.Serializer::class)
enum class SportActivity(
    val supportsDistanceMetrics: Boolean,
    val title: String
) {

    Cycling(true, "Cycling"),
    Gym(false, "Gym"),
    Running(true, "Running"),
    Swimming(true, "Swimming"),
    Walking(true, "Walking"),
    Yoga(false, "Yoga");

    companion object {

        fun fromTitle(title: String): SportActivity? {
            return entries.find {
                it.title.equals(
                    title,
                    ignoreCase = true
                )
            }
        }

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
}