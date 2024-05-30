package com.pvp.app.ui.screen.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.pvp.app.api.FeedbackService
import com.pvp.app.api.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackService: FeedbackService,
    private val userService: UserService
) : ViewModel() {

    fun create(
        bug: Boolean,
        description: String,
        rating: Int
    ) {
        viewModelScope.launch {
            userService.user
                .firstOrNull()
                ?.let { user ->
                    feedbackService.create(
                        bug = bug,
                        email = user.email,
                        description = description,
                        rating = rating,
                        date = Timestamp.now()
                    )
                }
        }
    }
}