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
import kotlinx.coroutines.flow.filterNotNull
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
        viewModelScope.launch(Dispatchers.IO) {
            userService.user
                .filterNotNull()
                .flatMapLatest { user ->
                    decorationService
                        .get()
                        .mapLatest { decorations ->
                            DecorationState(
                                avatar = user.avatar!!,
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
                }
                .collectLatest { state ->
                    _state.update {
                        it.copy(
                            avatar = state.avatar,
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
            try {
                val user = state.first().user

                decorationService.apply(
                    decoration,
                    user = user
                )

                val state = if (decoration.id in user.decorationsApplied) {
                    WorkState.Success.Unapply
                } else {
                    WorkState.Success.Apply
                }

                _state.update { it.copy(workState = state) }
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