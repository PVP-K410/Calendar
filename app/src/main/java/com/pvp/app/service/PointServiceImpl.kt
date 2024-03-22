package com.pvp.app.service

import com.pvp.app.api.PointService
import com.pvp.app.model.Task

class PointServiceImpl : PointService {

    override suspend fun calculate(
        task: Task
    ): Int {
        return 0
    }
}