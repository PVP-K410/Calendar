package com.pvp.app.ui.screen.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun fetchUserData() {
        viewModelScope.launch {
            _user.value = userService
                .getCurrent()
                .first()
        }
    }

    fun updateUserFilters(filters: List<String>, isActivities: Boolean) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val updatedUser = if (isActivities) {
                currentUser.copy(activities = filters.mapNotNull { SportActivity.fromTitle(it) })
            } else {
                currentUser.copy(ingredients = filters.mapNotNull { Ingredient.fromTitle(it) })
            }

            userService.merge(updatedUser)

            _user.emit(updatedUser)
        }
    }
}