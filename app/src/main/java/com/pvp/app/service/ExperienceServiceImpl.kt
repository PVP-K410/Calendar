package com.pvp.app.service

import com.pvp.app.api.ExperienceService
import javax.inject.Inject
import kotlin.math.sqrt

class ExperienceServiceImpl @Inject constructor() : ExperienceService {

    companion object {

        const val RATIO = 13
    }

    override fun experienceOf(
        level: Int
    ): Int {
        return when {
            level < 1 -> RATIO
            else -> level * level * RATIO
        }
    }

    override fun levelOf(
        experience: Int
    ): Int {
        return sqrt((experience.toDouble() / RATIO)).toInt()
    }
}