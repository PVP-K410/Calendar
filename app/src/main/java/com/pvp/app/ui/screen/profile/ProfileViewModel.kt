package com.pvp.app.ui.screen.profile

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.Configuration
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.FriendService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val configuration: Configuration,
    private val experienceService: ExperienceService,
    private val friendService: FriendService,
    private val userService: UserService,
    private val taskService: TaskService
) : ViewModel() {

    val state = userService.user
        .map { user ->
            ProfileState(
                experienceRequired = experienceService.experienceOf((user?.level ?: 0) + 1),
                isLoading = false,
                user = user ?: User(),
                userAvatar = userService.resolveAvatar(user?.email ?: "")
            )
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileState(
                experienceRequired = 0,
                isLoading = true,
                user = User(),
                userAvatar = ImageBitmap(1, 1)
            )
        )

    suspend fun deleteAccount(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val email = state.value.user.email

                taskService.get(email).first().forEach { task ->
                    taskService.remove(task)
                }

                friendService.remove(email)

                userService.remove(email)

                authenticationService.user.first()?.delete()?.await()

                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    fun update(
        function: (User) -> Unit
    ) {
        viewModelScope.launch {
            val user = state.first().user.copy()

            function(user)

            userService.merge(user)
        }
    }

    fun <T> fromConfiguration(function: (Configuration) -> T): T {
        return function(configuration)
    }
}

data class ProfileState(
    val experienceRequired: Int,
    val isLoading: Boolean,
    val user: User,
    val userAvatar: ImageBitmap
)