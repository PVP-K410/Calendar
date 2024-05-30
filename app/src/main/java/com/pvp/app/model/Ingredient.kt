package com.pvp.app.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pvp.app.R
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Ingredient.Companion.Serializer::class)
enum class Ingredient(@StringRes val titleId: Int) {

    Beans(R.string.ingredient_beans),
    Beef(R.string.ingredient_beef),
    Broccoli(R.string.ingredient_broccoli),
    Carrots(R.string.ingredient_carrots),
    Cheese(R.string.ingredient_cheese),
    Chicken(R.string.ingredient_chicken),
    Corn(R.string.ingredient_corn),
    Eggs(R.string.ingredient_eggs),
    Fish(R.string.ingredient_fish),
    Garlic(R.string.ingredient_garlic),
    Lamb(R.string.ingredient_lamb),
    Milk(R.string.ingredient_milk),
    Mushrooms(R.string.ingredient_mushrooms),
    Nuts(R.string.ingredient_nuts),
    Onions(R.string.ingredient_onions),
    Peanuts(R.string.ingredient_peanuts),
    Pork(R.string.ingredient_pork),
    Potatoes(R.string.ingredient_potatoes),
    Rice(R.string.ingredient_rice),
    Shellfish(R.string.ingredient_shellfish),
    Shrimp(R.string.ingredient_shrimp),
    Soybeans(R.string.ingredient_soybeans),
    Spinach(R.string.ingredient_spinach),
    Tofu(R.string.ingredient_tofu),
    Tomatoes(R.string.ingredient_tomatoes),
    Turkey(R.string.ingredient_turkey),
    Wheat(R.string.ingredient_wheat);

    val title: @Composable () -> String
        get() = { stringResource(titleId) }

    companion object {

        fun fromName(name: String): Ingredient? {
            return entries.find {
                it.name.equals(
                    name,
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
                return fromName(decoder.decodeString()) ?: error("Unknown ingredient")
            }

            override fun serialize(
                encoder: Encoder,
                value: Ingredient
            ) {
                encoder.encodeString(value.name)
            }
        }
    }
}