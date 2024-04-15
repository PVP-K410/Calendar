package com.pvp.app.ui.screen.decoration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.pvp.app.model.Decoration
import com.pvp.app.model.User
import com.pvp.app.ui.common.showToast

data class DecorationHolder(
    val applied: Boolean = false,
    val decoration: Decoration,
    val image: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val owned: Boolean = false
)

data class DecorationState(
    val avatar: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val holders: List<DecorationHolder> = emptyList(),
    val workState: WorkState = WorkState.NoOperation,
    val user: User = User()
)

sealed class WorkState {

    sealed class Error : WorkState() {

        data object AlreadyApplied : Error()

        data object AlreadyOwned : Error()

        data object InsufficientFunds : Error()

        companion object : Error()
    }

    data object Loading : WorkState()

    data object NoOperation : WorkState()

    sealed class Success : WorkState() {

        data object Apply : Error()

        data object Purchase : Error()

        companion object : Success()
    }

    @Composable
    fun WorkStateHandler(
        resetState: () -> Unit,
        state: WorkState
    ) {
        val context = LocalContext.current

        LaunchedEffect(state) {
            when (state) {
                is NoOperation -> return@LaunchedEffect

                is Success -> when (state) {
                    is Success.Apply -> context.showToast(message = "Successfully applied decoration")
                    is Success.Purchase -> context.showToast(message = "Successfully purchased decoration")

                    else -> context.showToast(message = "Success")
                }

                is Error -> when (state) {
                    is Error.AlreadyApplied -> context.showToast(message = "You already have this decoration applied")
                    is Error.AlreadyOwned -> context.showToast(message = "You already own this decoration")
                    is Error.InsufficientFunds -> context.showToast(message = "Not enough points to purchase decoration")

                    else -> context.showToast(message = "Error has occurred")

                }

                else -> {}
            }

            resetState()
        }
    }
}