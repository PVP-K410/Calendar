@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.decoration

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.DecorationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Decoration
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DecorationViewModel @Inject constructor(
    private val decorationService: DecorationService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(DecorationState())

    val state = _state.asStateFlow()

    init {
        collectStateChanges()
    }

    private fun collectStateChanges() {
        viewModelScope.launch {
            userService.user
                .flatMapLatest { user ->
                    decorationService
                        .get()
                        .mapLatest { decorations ->
                            DecorationState(
                                avatar = user?.avatar ?: ImageBitmap(
                                    1,
                                    1
                                ),
                                holders = decorations.map { decoration ->
                                    DecorationHolder(
                                        applied = user?.decorationsApplied?.contains(decoration.id)
                                            ?: false,
                                        decoration = decoration,
                                        owned = user?.decorationsOwned?.contains(decoration.id)
                                            ?: false
                                    )
                                },
                                user = user ?: User()
                            )
                        }
                }
                .collectLatest { state ->
                    _state.update {
                        it.copy(
                            holders = state.holders,
                            user = state.user
                        )
                    }
                }
        }
    }

    fun apply(decoration: Decoration) {
        _state.update { it.copy(workState = WorkState.Loading) }

        viewModelScope.launch {
            val state = _state.first()

            if (state.user.decorationsApplied.contains(decoration.id)) {
                _state.update { it.copy(workState = WorkState.Error.AlreadyApplied) }

                return@launch
            }

            try {
                _state.update {
                    it.copy(
                        avatar = decorationService.apply(
                            it.avatar,
                            decoration
                        ),
                        workState = WorkState.Success.Apply
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(workState = WorkState.Error) }
            }
        }
    }

    fun purchase(decoration: Decoration) {
        _state.update { it.copy(workState = WorkState.Loading) }

        viewModelScope.launch {
            val state = _state.first()
            val holder = state.holders.first { it.decoration == decoration }

            if (holder.owned) {
                _state.update { it.copy(workState = WorkState.Error.AlreadyOwned) }

                return@launch
            }

            if (state.user.points < decoration.price) {
                _state.update { it.copy(workState = WorkState.Error.InsufficientFunds) }

                return@launch
            }

            try {
                decorationService.purchase(
                    decoration,
                    state.user
                )

                _state.update { it.copy(workState = WorkState.Success.Purchase) }
            } catch (e: Exception) {
                _state.update { it.copy(workState = WorkState.Error) }

                return@launch
            }
        }
    }

    fun resetWorkState() {
        _state.update { it.copy(workState = WorkState.NoOperation) }
    }
}