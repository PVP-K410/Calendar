@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.decoration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.DecorationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Decoration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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

    private val screenStateAwaited = MutableStateFlow<DecorationScreenState>(
        DecorationScreenState.NoOperation
    )

    init {
        collectStateChanges()
    }

    private fun collectStateChanges() {
        val userFlow = userService.user.filterNotNull()

        viewModelScope.launch(Dispatchers.IO) {
            userFlow
                .combine(decorationService.get()) { user, decorations ->
                    DecorationState(
                        holders = decorations
                            .map { decoration ->
                                DecorationHolder(
                                    applied = decoration.id in user.decorationsApplied,
                                    decoration = decoration,
                                    owned = decoration.id in user.decorationsOwned
                                )
                            }
                            .sortedWith(
                                compareBy<DecorationHolder> { it.decoration.type }
                                    .thenBy { it.decoration.price }
                            ),
                        user = user
                    )
                }
                .collectLatest { state ->
                    _state.update {
                        it.copy(
                            holders = state.holders,
                            state = when (it.state) {
                                is DecorationScreenState.Loading.LoadingPurchase -> {
                                    val stateNew = screenStateAwaited.value

                                    screenStateAwaited.update { DecorationScreenState.NoOperation }

                                    stateNew
                                }

                                is DecorationScreenState.Loading.Companion -> DecorationScreenState.NoOperation
                                else -> it.state
                            },
                            user = state.user
                        )
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            decorationService
                .getAvatar(userFlow)
                .collectLatest { avatar ->
                    _state.update {
                        it.copy(
                            avatar = avatar,
                            state = when (it.state) {
                                is DecorationScreenState.Loading.LoadingApply -> {
                                    val stateNew = screenStateAwaited.value

                                    screenStateAwaited.update { DecorationScreenState.NoOperation }

                                    stateNew
                                }

                                is DecorationScreenState.Loading.Companion -> DecorationScreenState.NoOperation
                                else -> it.state
                            }
                        )
                    }
                }
        }
    }

    fun apply(decoration: Decoration) {
        _state.update { it.copy(state = DecorationScreenState.Loading.LoadingApply) }

        viewModelScope.launch(Dispatchers.IO) {
            val state = _state.first()

            try {
                val stateWork = if (decoration.id in state.user.decorationsApplied) {
                    DecorationScreenState.Success.Unapply
                } else {
                    DecorationScreenState.Success.Apply
                }

                screenStateAwaited.update { stateWork }

                decorationService.apply(
                    decoration,
                    user = state.user
                )
            } catch (e: Exception) {
                _state.update { it.copy(state = DecorationScreenState.Error) }
            }
        }
    }

    fun purchase(decoration: Decoration) {
        _state.update { it.copy(state = DecorationScreenState.Loading.LoadingPurchase) }

        viewModelScope.launch(Dispatchers.IO) {
            val state = _state.first()
            val holder = state.holders.first { it.decoration == decoration }

            if (holder.owned) {
                _state.update { it.copy(state = DecorationScreenState.Error.AlreadyOwned) }

                return@launch
            }

            if (state.user.points < decoration.price) {
                _state.update { it.copy(state = DecorationScreenState.Error.InsufficientFunds) }

                return@launch
            }

            try {
                screenStateAwaited.update { DecorationScreenState.Success.Purchase }

                decorationService.purchase(
                    decoration,
                    state.user
                )
            } catch (e: Exception) {
                _state.update { it.copy(state = DecorationScreenState.Error) }

                return@launch
            }
        }
    }

    fun resetScreenState() {
        _state.update { it.copy(state = DecorationScreenState.NoOperation) }
    }
}