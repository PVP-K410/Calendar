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

    Gym(0, false, "Gym"),
    Badminton(2, false, "Badminton"),
    Baseball(4, false, "Baseball"),
    Basketball(5, false, "Basketball"),
    Cycling(8, true, "Cycling"),
    CyclingStationary(9, false, "Cycling (Stationary)"),
    BootCamp(10, false, "Boot Camp"),
    Boxing(11, false, "Boxing"),
    Calisthenics(13, false, "Calisthenics"),
    Cricket(14, false, "Cricket"),
    Dancing(16, false, "Dancing"),
    Elliptical(25, false, "Elliptical"),
    ExerciseClass(26, false, "Exercise Class"),
    Fencing(27, false, "Fencing"),
    FootballAmerican(28, false, "Football (American)"),
    FootballAustralian(29, false, "Football (Australian)"),
    FrisbeeDisc(31, false, "Frisbee Disc"),
    Golf(32, false, "Golf"),
    GuidedBreathing(33, false, "Guided Breathing"),
    Gymnastics(34, false, "Gymnastics"),
    Handball(35, false, "Handball"),
    HighIntensityIntervalTraining(36, false, "High Intensity Interval Training"),
    Hiking(37, true, "Hiking"),
    IceHockey(38, false, "Ice Hockey"),
    IceSkating(39, false, "Ice Skating"),
    MartialArts(44, false, "Martial Arts"),
    Paddling(46, true, "Paddling"),
    Paragliding(47, false, "Paragliding"),
    Pilates(48, false, "Pilates"),
    Racquetball(50, false, "Racquetball"),
    RockClimbing(51, true, "Rock Climbing"),
    RollerHockey(52, false, "Roller Hockey"),
    Rowing(53, true, "Rowing"),
    RowingMachine(54, false, "Rowing Machine"),
    Rugby(55, false, "Rugby"),
    Running(56, true, "Running"),
    RunningTreadmill(57, true, "Running (Treadmill)"),
    Sailing(58, false, "Sailing"),
    ScubaDiving(59, false, "Scuba Diving"),
    Skating(60, false, "Skating"),
    Skiing(61, true, "Skiing"),
    Snowboarding(62, true, "Snowboarding"),
    Snowshoeing(63, true, "Snowshoeing"),
    Soccer(64, false, "Soccer"),
    Softball(65, false, "Softball"),
    Squash(66, false, "Squash"),
    StairClimbing(68, true, "Stair Climbing"),
    StairClimbingMachine(69, false, "Stair Climbing Machine"),
    StrengthTraining(70, false, "Strength Training"),
    Stretching(71, false, "Stretching"),
    Surfing(72, true, "Surfing"),
    SwimmingOpenWater(73, true, "Swimming (Open Water)"),
    SwimmingPool(74, true, "Swimming (Pool)"),
    TableTennis(75, false, "Table Tennis"),
    Tennis(76, false, "Tennis"),
    Volleyball(78, false, "Volleyball"),
    Walking(79, true, "Walking"),
    WaterPolo(80, false, "Water Polo"),
    Weightlifting(81, false, "Weightlifting"),
    Wheelchair(82, true, "Wheelchair"),
    Yoga(83, false, "Yoga");

    companion object {

        fun fromId(id: Int): SportActivity? {
            return entries.find {
                it.id == id
            }
        }

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
