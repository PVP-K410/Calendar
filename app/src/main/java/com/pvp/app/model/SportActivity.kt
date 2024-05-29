package com.pvp.app.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Accessible
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.DownhillSkiing
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.IceSkating
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Rowing
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Snowboarding
import androidx.compose.material.icons.outlined.SportsBaseball
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material.icons.outlined.SportsGolf
import androidx.compose.material.icons.outlined.SportsGymnastics
import androidx.compose.material.icons.outlined.SportsHandball
import androidx.compose.material.icons.outlined.SportsHockey
import androidx.compose.material.icons.outlined.SportsMma
import androidx.compose.material.icons.outlined.SportsRugby
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsTennis
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.pvp.app.R
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

    @StringRes val titleId: Int,

    /**
     * Icon representing the sport activity
     */
    val icon: ImageVector
) {

    Badminton(2, false, 0.0f, 0.6f, R.string.activity_badminton, Icons.Outlined.SportsTennis),
    Baseball(4, false, 0.0f, 0.6f, R.string.activity_baseball, Icons.Outlined.SportsBaseball),
    Basketball(5, false, 0.0f, 0.6f, R.string.activity_basketball, Icons.Outlined.SportsBasketball),
    Boxing(11, false, 0.0f, 0.85f, R.string.activity_boxing, Icons.Outlined.SportsMma),
    Cricket(14, false, 0.0f, 0.5f, R.string.activity_cricket, Icons.Outlined.SportsCricket),
    Cycling(8, true, 0.15f, 0.0f, R.string.activity_cycling, Icons.AutoMirrored.Outlined.DirectionsBike),
    Football(64, false, 0.0f, 0.6f, R.string.activity_football, Icons.Outlined.SportsSoccer),
    Golf(32, false, 0.0f, 0.1f, R.string.activity_golf, Icons.Outlined.SportsGolf),
    Gymnastics(34, false, 0.0f, 0.3f, R.string.activity_gymnastics, Icons.Outlined.SportsGymnastics),
    Handball(35, false, 0.0f, 0.7f, R.string.activity_handball, Icons.Outlined.SportsHandball),
    Hiking(37, true, 0.5f, 0.0f, R.string.activity_hiking, Icons.Outlined.Hiking),
    IceHockey(38, false, 0.0f, 0.7f, R.string.activity_ice_hockey, Icons.Outlined.SportsHockey),
    IceSkating(39, false, 0.0f, 0.3f, R.string.activity_ice_skating, Icons.Outlined.IceSkating),
    Other(0, false, 0.0f, 0.1f, R.string.activity_other, Icons.Outlined.FitnessCenter),
    Pilates(48, false, 0.0f, 0.1f, R.string.activity_pilates, Icons.Outlined.SelfImprovement),
    RockClimbing(51, false, 0.85f, 0.0f, R.string.activity_rock_climbing, Icons.Outlined.Landscape),
    Rowing(53, true, 0.7f, 0.0f, R.string.activity_rowing, Icons.Outlined.Rowing),
    Rugby(55, false, 0.0f, 1f, R.string.activity_rugby, Icons.Outlined.SportsRugby),
    Running(56, true, 0.4f, 0.0f, R.string.activity_running, Icons.AutoMirrored.Outlined.DirectionsRun),
    Skiing(61, true, 0.6f, 0.0f, R.string.activity_skiing, Icons.Outlined.DownhillSkiing),
    Snowboarding(62, true, 0.9f, 0.0f, R.string.activity_snowboarding, Icons.Outlined.Snowboarding),
    Softball(65, false, 0.0f, 0.5f, R.string.activity_softball, Icons.Outlined.SportsBaseball),
    Squash(66, false, 0.0f, 0.6f, R.string.activity_squash, Icons.Outlined.SportsTennis),
    Swimming(74, true, 0.5f, 0.0f, R.string.activity_swimming, Icons.Outlined.Pool),
    TableTennis(75, false, 0.0f, 0.5f, R.string.activity_table_tennis, Icons.Outlined.SportsTennis),
    Tennis(76, false, 0.0f, 0.7f, R.string.activity_tennis, Icons.Outlined.SportsTennis),
    Volleyball(78, false, 0.0f, 0.6f, R.string.activity_volleyball, Icons.Outlined.SportsVolleyball),
    Walking(79, true, 0.25f, 0.0f, R.string.activity_walking, Icons.AutoMirrored.Outlined.DirectionsWalk),
    Wheelchair(82, true, 1f, 0.0f, R.string.activity_wheelchair, Icons.AutoMirrored.Outlined.Accessible),
    Yoga(83, false, 0.0f, 0.1f, R.string.activity_yoga, Icons.Outlined.SelfImprovement);

    /**
     * Title of the sport activity
     */
    val title: @Composable () -> String
        get() = { stringResource(titleId) }

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

        fun fromName(name: String): SportActivity {
            return entries.find {
                it.name.equals(
                    name,
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
                return fromName(decoder.decodeString())
            }

            override fun serialize(encoder: Encoder, value: SportActivity) {
                encoder.encodeString(value.name)
            }
        }
    }
}
