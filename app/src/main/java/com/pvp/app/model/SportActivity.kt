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
    val id: Int,
    val supportsDistanceMetrics: Boolean,
    val title: String
) {

    Other(0, false, "Other"),
    Badminton(2, false, "Badminton"),
    Baseball(4, false, "Baseball"),
    Basketball(5, false, "Basketball"),
    Cycling(8, true, "Cycling"),
    Boxing(11, false, "Boxing"),
    Cricket(14, false, "Cricket"),
    Golf(32, false, "Golf"),
    Gymnastics(34, false, "Gymnastics"),
    Handball(35, false, "Handball"),
    Hiking(37, true, "Hiking"),
    IceHockey(38, false, "Ice Hockey"),
    IceSkating(39, false, "Ice Skating"),
    Pilates(48, false, "Pilates"),
    RockClimbing(51, true, "Rock Climbing"),
    Rowing(53, true, "Rowing"),
    Rugby(55, false, "Rugby"),
    Running(56, true, "Running"),
    Skiing(61, true, "Skiing"),
    Snowboarding(62, true, "Snowboarding"),
    Football(64, false, "Football"),
    Softball(65, false, "Softball"),
    Squash(66, false, "Squash"),
    Swimming(74, true, "Swimming"),
    TableTennis(75, false, "Table Tennis"),
    Tennis(76, false, "Tennis"),
    Volleyball(78, false, "Volleyball"),
    Walking(79, true, "Walking"),
    Wheelchair(82, true, "Wheelchair"),
    Yoga(83, false, "Yoga");

    companion object {

        fun fromId(id: Int): SportActivity? {
            return entries.find {
                it.id == id
            } ?: Other
        }

        fun fromTitle(title: String): SportActivity? {
            return entries.find {
                it.title.equals(
                    title,
                    ignoreCase = true
                )
            } ?: Other
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
