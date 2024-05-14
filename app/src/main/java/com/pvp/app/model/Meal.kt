@file:OptIn(ExperimentalSerializationApi::class)

package com.pvp.app.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class CaloricBreakdown(
    val percentCarbs: Double = 0.0,
    val percentFat: Double = 0.0,
    val percentProtein: Double = 0.0
)

@Serializable
data class Instructions(
    val name: String = "",
    val steps: List<Step> = emptyList()
)

@Serializable
data class Length(
    val number: Int = 0,
    val unit: String = ""
)

@Serializable
data class Meal(
    val dishTypes: List<String> = emptyList(),
    val diets: List<String> = emptyList(),
    val id: String = "",
    val image: String = "",
    val nutrition: Nutrition = Nutrition(),
    @JsonNames("instructions")
    val recipe: List<Instructions> = emptyList(),
    val readyInMinutes: Int = 0,
    val servings: Int = 0,
    @JsonNames("title")
    val name: String = ""
)

@Serializable
data class Nutrient(
    val amount: Double = 0.0,
    val name: String = "",
    val percentOfDailyNeeds: Double = 0.0,
    val unit: String = ""
)

@Serializable
data class Nutrition(
    val caloricBreakdown: CaloricBreakdown = CaloricBreakdown(),
    val nutrients: List<Nutrient> = emptyList(),
    val weightPerServing: WeightPerServing = WeightPerServing()
)

@Serializable
data class Step(
    val ingredients: List<String> = emptyList(),
    val length: Length? = null,
    val number: Int = 0,
    val step: String = ""
)

@Serializable
data class WeightPerServing(
    val amount: Int = 0,
    val unit: String = ""
)