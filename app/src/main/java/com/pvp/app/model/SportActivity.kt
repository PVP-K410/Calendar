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

    /**
     * Unique identifier of the sport activity, used with the HealthConnect API
     */
    val id: Int,

    /**
     * Whether the sport activity supports distance metrics. If it doesn't, [pointsRatioDistance]
     * is ignored and the distance is not taken into account when calculating [SportTask] points
     */
    val supportsDistanceMetrics: Boolean,

    /**
     * Points ratio for the distance of the sport activity. The distance is taken into account when
     * calculating [SportTask] points. Greater ratio yield more points. Interval is [0, 1]
     */
    val pointsRatioDistance: Float,

    /**
     * Points ratio for the duration of the sport activity. The duration is taken into account when
     * calculating [SportTask] points. Greater ratio yield more points. Interval is [0, 1]
     */
    val pointsRatioDuration: Float,

    /**
     * Title of the sport activity
     */
    val title: String
) {

    Badminton(2, false, 0.0f, 0.6f, "Badminton"),
    Baseball(4, false, 0.0f, 0.6f, "Baseball"),
    Basketball(5, false, 0.0f, 0.6f, "Basketball"),
    Boxing(11, false, 0.0f, 0.85f, "Boxing"),
    Cricket(14, false, 0.0f, 0.5f, "Cricket"),
    Cycling(8, true, 0.15f, 0.0f, "Cycling"),
    Football(64, false, 0.0f, 0.6f, "Football"),
    Golf(32, false, 0.0f, 0.1f, "Golf"),
    Gymnastics(34, false, 0.0f, 0.3f, "Gymnastics"),
    Handball(35, false, 0.0f, 0.7f, "Handball"),
    Hiking(37, true, 0.5f, 0.0f, "Hiking"),
    IceHockey(38, false, 0.0f, 0.7f, "Ice Hockey"),
    IceSkating(39, false, 0.0f, 0.3f, "Ice Skating"),
    Other(0, false, 0.0f, 0.1f, "Other"),
    Pilates(48, false, 0.0f, 0.1f, "Pilates"),
    RockClimbing(51, true, 0.85f, 0.0f, "Rock Climbing"),
    Rowing(53, true, 0.7f, 0.0f, "Rowing"),
    Rugby(55, false, 0.0f, 1f, "Rugby"),
    Running(56, true, 0.4f, 0.0f, "Running"),
    Skiing(61, true, 0.6f, 0.0f, "Skiing"),
    Snowboarding(62, true, 0.9f, 0.0f, "Snowboarding"),
    Softball(65, false, 0.0f, 0.5f, "Softball"),
    Squash(66, false, 0.0f, 0.6f, "Squash"),
    Swimming(74, true, 0.5f, 0.0f, "Swimming"),
    TableTennis(75, false, 0.0f, 0.5f, "Table Tennis"),
    Tennis(76, false, 0.0f, 0.7f, "Tennis"),
    Volleyball(78, false, 0.0f, 0.6f, "Volleyball"),
    Walking(79, true, 0.25f, 0.0f, "Walking"),
    Wheelchair(82, true, 1f, 0.0f, "Wheelchair"),
    Yoga(83, false, 0.0f, 0.1f, "Yoga");

    init {
        require(pointsRatioDistance in 0.0..1.0) {
            "Points ratio for distance must be in [0, 1]"
        }

        require(pointsRatioDuration in 0.0..1.0) {
            "Points ratio for duration must be in [0, 1]"
        }
    }

    companion object {

        fun fromId(id: Int): SportActivity {
            return entries.find {
                it.id == id
            } ?: Other
        }

        fun fromTitle(title: String): SportActivity {
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
                return fromTitle(decoder.decodeString())
            }

            override fun serialize(encoder: Encoder, value: SportActivity) {
                encoder.encodeString(value.title)
            }
        }
    }
}
