package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.ui.screen.decoration.WorkState.Error.AlreadyOwned.WorkStateHandler

@Composable
fun DecorationSelect(model: DecorationViewModel = hiltViewModel()) {
    val state by model.state.collectAsStateWithLifecycle()

    WorkStateHandler(
        resetState = model::resetWorkState,
        state = state.workState
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                contentDescription = "User avatar with applied decorations",
                modifier = Modifier.size(
                    height = 312.dp,
                    width = 256.dp
                ),
                painter = BitmapPainter(state.avatar)
            )
        }

        DecorationCards(
            actionImageVector = Icons.Outlined.TouchApp,
            actionPurchase = false,
            holders = state.holders,
        ) { model.apply(it.decoration) }
    }
}