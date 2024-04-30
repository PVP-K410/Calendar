@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.ActivityService
import com.pvp.app.api.UserService
import com.pvp.app.model.ActivityEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val activityService: ActivityService,
    private val userService: UserService
) : ViewModel() {

    val state = run {
        val userFlow = userService.user.filterNotNull()

        val valuesWeekFlow = userFlow.flatMapLatest { user ->
            val now = LocalDate.now()

            activityService.get(
                now.with(DayOfWeek.MONDAY) to now,
                user.email
            )
        }

        val valuesMonthFlow = userFlow.flatMapLatest { user ->
            val now = LocalDate.now()

            activityService.get(
                now.withDayOfMonth(1) to now,
                user.email
            )
        }

        val values7dFlow = userFlow.flatMapLatest { user ->
            val now = LocalDate.now()

            activityService.get(
                now.minusDays(7) to now,
                user.email
            )
        }

        val values30dFlow = userFlow.flatMapLatest { user ->
            val now = LocalDate.now()

            activityService.get(
                now.minusDays(30) to now,
                user.email
            )
        }

        combine(
            valuesWeekFlow.onStart { emit(emptyList()) },
            valuesMonthFlow.onStart { emit(emptyList()) },
            values7dFlow.onStart { emit(emptyList()) },
            values30dFlow.onStart { emit(emptyList()) }
        ) { valuesWeek, valuesMonth, values7d, values30d ->
            AnalysisState(
                isLoading = false,
                valuesWeek = valuesWeek,
                valuesMonth = valuesMonth,
                values7d = values7d,
                values30d = values30d
            )
        }
            .flowOn(Dispatchers.IO)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                AnalysisState()
            )
    }
}

data class AnalysisState(
    val isLoading: Boolean = true,
    val valuesWeek: List<ActivityEntry> = listOf(),
    val valuesMonth: List<ActivityEntry> = listOf(),
    val values7d: List<ActivityEntry> = listOf(),
    val values30d: List<ActivityEntry> = listOf()
)