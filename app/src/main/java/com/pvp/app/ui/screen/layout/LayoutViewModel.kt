package com.pvp.app.ui.screen.layout

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.DecorationService
import com.pvp.app.api.RewardService
import com.pvp.app.api.StreakService
import com.pvp.app.api.UserService
import com.pvp.app.model.Reward
import com.pvp.app.model.Survey
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LayoutViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val decorationService: DecorationService,
    private val userService: UserService,
    private val rewardService: RewardService,
    private val streakService: StreakService
) : ViewModel() {

    private val _state = MutableStateFlow(LayoutState())
    val state: StateFlow<LayoutState> = _state.asStateFlow()

    private val _reward = MutableStateFlow(Reward())
    val reward: StateFlow<Reward> = _reward.asStateFlow()

    init {
        collectStateUpdates()
    }

    private fun collectStateUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                userService.user,
                authenticationService.user
            ) { userApp, userFirebase ->
                LayoutState(
                    areSurveysFilled = userApp?.let { areSurveysFilled(it) },
                    isAuthenticated = userFirebase != null,
                    isLoading = false,
                    user = userApp,
                    needsStreakReward = streakService.checkStreak()
                )
            }
                .collectLatest { state ->
                    _state.update {
                        it.copy(
                            areSurveysFilled = state.areSurveysFilled,
                            isAuthenticated = state.isAuthenticated,
                            isLoading = state.isLoading,
                            user = state.user,
                            needsStreakReward = state.needsStreakReward
                        )
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            decorationService
                .getAvatar(userService.user.filterNotNull())
                .collectLatest { avatar -> _state.update { it.copy(avatar = avatar) } }
        }
    }

    private fun areSurveysFilled(user: User): Boolean {
        return user.surveys.containsAll(Survey.entries)
    }

    suspend fun giveReward() {
        viewModelScope.launch(Dispatchers.IO) {
            _reward.value = rewardService.get()

            rewardService.rewardUser(
                _reward.value
            )
        }
    }
}

data class LayoutState(
    val areSurveysFilled: Boolean? = null,
    val avatar: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = true,
    val user: User? = null,
    val needsStreakReward: Boolean = false
)