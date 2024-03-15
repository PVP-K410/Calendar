package com.pvp.app.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Survey.Companion.Serializer::class)
enum class Survey {

    BODY_MASS_INDEX,
    FILTER_ACTIVITIES;

    companion object {

        fun fromId(id: String): Survey? {
            return entries.find {
                it.name.equals(
                    id,
                    ignoreCase = true
                )
            }
        }

        object Serializer : KSerializer<Survey> {

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
                "com.pvp.app.model.Survey",
                PrimitiveKind.STRING
            )

            override fun deserialize(decoder: Decoder): Survey {
                return fromId(decoder.decodeString()) ?: error("Unknown survey")
            }

            override fun serialize(encoder: Encoder, value: Survey) {
                encoder.encodeString(value.name)
            }
        }
    }
}