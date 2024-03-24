package com.pvp.app.service

import com.pvp.app.api.ExperienceService
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

class ExperienceServiceImpl @Inject constructor(

) : ExperienceService {

    companion object {

        const val BASE = 100
        const val RATIO = 1.1
    }

    override fun experienceOf(
        level: Int
    ): Int {
        return (BASE * RATIO.pow(level - 1)).toInt()
    }

    override fun levelOf(
        experience: Int
    ): Int {
        if (experience <= BASE) {
            return 1
        }

        return ceil(ln(experience.toDouble() / BASE) / ln(RATIO)).toInt()
    }
}