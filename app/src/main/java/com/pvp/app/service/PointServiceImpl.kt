package com.pvp.app.service

import com.pvp.app.api.PointService
import com.pvp.app.model.Task
import javax.inject.Inject

class PointServiceImpl @Inject constructor(

) : PointService {

    override suspend fun calculate(
        task: Task
    ): Int {
        return 0
    }
}