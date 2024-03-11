package com.pvp.app.ui.screen.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsSurveyViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    fun updateBodyMassIndex(mass: Int, height: Int) {
        viewModelScope.launch {
            val user = userService
                .getCurrent()
                .firstOrNull()

            if (user != null) {
                userService.merge(
                    user.copy(
                        mass = mass,
                        height = height
                    )
                )
            }
        }
    }
}