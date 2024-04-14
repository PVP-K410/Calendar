package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.ui.screen.decoration.WorkState.Error.AlreadyOwned.WorkStateHandler

@Composable
fun DecorationPurchase(model: DecorationViewModel = hiltViewModel()) {
    val state by model.state.collectAsStateWithLifecycle()

    WorkStateHandler(
        resetState = model::resetWorkState,
        state = state.workState
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        DecorationCards(
            actionImageVector = Icons.Outlined.ShoppingBag,
            actionPurchase = true,
            holders = state.holders,
        ) { model.purchase(it.decoration) }
    }
}