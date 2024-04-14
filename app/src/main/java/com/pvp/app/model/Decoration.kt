package com.pvp.app.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Decoration(
    val description: String? = null,
    val id: String = "",
    val imageUrl: String = "",
    val name: String? = null,
    val price: Int,
    val type: Type = Type.None
)

@Serializable(Type.Companion.Serializer::class)
sealed class Type {

    data object None : Type()

    sealed class Avatar : Type() {

        data object Face : Avatar()

        data object Head : Avatar()

        data object Pants : Avatar()

        data object Shirt : Avatar()

        data object Shoes : Avatar()
    }

    companion object {

        data object Serializer : KSerializer<Type> {

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
                "DecorationType",
                PrimitiveKind.STRING
            )

            override fun deserialize(decoder: Decoder): Type =
                when (val value = decoder.decodeString()) {
                    "None" -> None

                    "Avatar.Face" -> Avatar.Face
                    "Avatar.Head" -> Avatar.Head
                    "Avatar.Pants" -> Avatar.Pants
                    "Avatar.Shirt" -> Avatar.Shirt
                    "Avatar.Shoes" -> Avatar.Shoes

                    else -> throw IllegalArgumentException("Unknown Type: $value")
                }

            override fun serialize(
                encoder: Encoder,
                value: Type
            ) = encoder.encodeString(
                when (value) {
                    None -> "None"

                    is Avatar -> "Avatar.${
                        when (value) {
                            Avatar.Face -> "Face"
                            Avatar.Head -> "Head"
                            Avatar.Pants -> "Pants"
                            Avatar.Shirt -> "Shirt"
                            Avatar.Shoes -> "Shoes"
                        }
                    }"
                }
            )
        }
    }
}