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
    val owned: Boolean = false
)

data class DecorationState(
    val avatar: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val holders: List<DecorationHolder> = emptyList(),
    val state: DecorationScreenState = DecorationScreenState.NoOperation,
    val user: User = User()
)

sealed class DecorationScreenState {

    sealed class Error : DecorationScreenState() {

        data object AlreadyOwned : Error()

        data object InsufficientFunds : Error()

        companion object : Error()
    }

    data object Loading : DecorationScreenState()

    data object NoOperation : DecorationScreenState()

    sealed class Success : DecorationScreenState() {

        data object Apply : Success()

        data object Purchase : Success()

        data object Unapply : Success()

        companion object : Success()
    }

    companion object {

        @Composable
        fun ScreenStateHandler(
            resetState: () -> Unit,
            state: DecorationScreenState
        ) {
            val context = LocalContext.current

            LaunchedEffect(state) {
                when (state) {
                    is NoOperation -> return@LaunchedEffect

                    is Success -> when (state) {
                        is Success.Apply -> context.showToast(message = "Successfully applied decoration")
                        is Success.Purchase -> context.showToast(message = "Successfully purchased decoration")
                        is Success.Unapply -> context.showToast(message = "Successfully removed decoration")

                        else -> context.showToast(message = "Success")
                    }

                    is Error -> when (state) {
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
}