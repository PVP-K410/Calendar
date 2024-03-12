package com.pvp.app.ui.screen.survey

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    private val _form = MutableStateFlow(Form.BODY_MASS_INDEX)
    val form = _form.asStateFlow()

    private val user = MutableStateFlow<User?>(null)

    init {
        viewModelScope.launch {
            userService
                .getCurrent()
                .map {
                    user.value = it
                }
                .launchIn(viewModelScope)
        }
    }

    fun next() {
        form.value.next?.let {
            _form.value = it
        }
    }

    fun updateBodyMassIndex(
        height: Int,
        mass: Int
    ) {
        viewModelScope.launch {
            user.value?.let {
                userService.merge(
                    it.copy(
                        height = height,
                        mass = mass
                    )
                )
            }
        }
    }
}

enum class Form(
    val content: @Composable (Modifier, SurveyViewModel) -> () -> Boolean,
    val next: Form?
) {

    BODY_MASS_INDEX(
        { m, vm ->
            BodyMassIndexSurvey(
                modifier = m,
                onSubmit = { height, mass ->
                    vm.updateBodyMassIndex(
                        height = height,
                        mass = mass
                    )
                }
            )
        },
        null
    );
    // TODO: Append new survey forms here.

    fun hasNext() = next != null
}