package com.pvp.app.ui.screen.profile

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.Configuration
import com.pvp.app.api.DecorationService
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.FriendService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val configuration: Configuration,
    private val decorationService: DecorationService,
    private val experienceService: ExperienceService,
    private val friendService: FriendService,
    private val userService: UserService,
    private val taskService: TaskService
) : ViewModel() {

    private val _state: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val rangeMass = configuration.rangeMass
    val rangeHeight = configuration.rangeHeight
    val rangeStepsPerDayGoal = configuration.rangeStepsPerDayGoal
    val intervalUsernameLength = configuration.intervalUsernameLength

    init {
        collectStateChanges()
    }

    private fun collectStateChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            val flow = userService.user.filterNotNull()

            flow
                .combine(decorationService.getAvatar(flow)) { user, avatar ->
                    ProfileState(
                        avatar = avatar,
                        experienceRequired = experienceService.experienceOf(user.level + 1),
                        isLoading = false,
                        user = user
                    )
                }
                .collectLatest { state -> _state.update { state } }
        }
    }

    suspend fun deleteAccount(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val email = state.value.user.email

                taskService.removeAll(email)
                friendService.remove(email)
                userService.remove(email)
                authenticationService.deleteAccount()

                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun update(function: (User) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _state.first().user.copy()

            function(user)

            userService.merge(user)
        }
    }
}

data class ProfileState(
    val avatar: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val experienceRequired: Int = 0,
    val isLoading: Boolean = true,
    val user: User = User()
)