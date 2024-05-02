package com.pvp.app.ui.screen.decoration

import androidx.compose.ui.graphics.ImageBitmap
import com.pvp.app.model.Decoration
import com.pvp.app.model.User

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
    val state: DecorationScreenState = DecorationScreenState.Loading,
    val user: User = User()
)

sealed class DecorationScreenState {

    sealed class Error : DecorationScreenState() {

        data object AlreadyOwned : Error()

        data object InsufficientFunds : Error()

        companion object : Error()
    }

    sealed class Loading : DecorationScreenState() {

        data object LoadingApply : DecorationScreenState()

        data object LoadingPurchase : DecorationScreenState()

        companion object : Loading()
    }

    data object NoOperation : DecorationScreenState()

    sealed class Success : DecorationScreenState() {

        data object Apply : Success()

        data object Purchase : Success()

        data object Unapply : Success()

        companion object : Success()
    }
}