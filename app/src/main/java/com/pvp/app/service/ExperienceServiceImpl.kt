package com.pvp.app.service

import com.pvp.app.api.ExperienceService
import javax.inject.Inject
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
        return (BASE * RATIO.pow(level)).toInt()
    }

    override fun levelOf(
        experience: Int
    ): Int {
        return (ln(experience.toDouble() / BASE) / ln(RATIO)).toInt()
    }
}