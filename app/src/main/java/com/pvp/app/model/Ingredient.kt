package com.pvp.app.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Ingredient.Companion.Serializer::class)
enum class Ingredient(
    val title: String
) {

    Beans("Beans"),
    Beef("Beef"),
    Broccoli("Broccoli"),
    Carrots("Carrots"),
    Cheese("Cheese"),
    Chicken("Chicken"),
    Fish("Fish"),
    Garlic("Garlic"),
    Milk("Milk"),
    Mushrooms("Mushrooms"),
    Onions("Onions"),
    Pork("Pork"),
    Potatoes("Potatoes"),
    Rice("Rice"),
    Shrimp("Shrimp"),
    Spinach("Spinach"),
    Tomatoes("Tomatoes");

    companion object {

        fun fromTitle(title: String): Ingredient? {
            return entries.find {
                it.title.equals(
                    title,
                    ignoreCase = true
                )
            }
        }

        object Serializer : KSerializer<Ingredient> {

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
                "com.pvp.app.model.Ingredients",
                PrimitiveKind.STRING
            )

            override fun deserialize(decoder: Decoder): Ingredient {
                return fromTitle(decoder.decodeString()) ?: error("Unknown ingredient")
            }

            override fun serialize(encoder: Encoder, value: Ingredient) {
                encoder.encodeString(value.title)
            }
        }
    }
}