@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.ui.common.underline
import com.pvp.app.ui.screen.decoration.WorkState.Companion.WorkStateHandler

@Composable
private fun Apply(model: DecorationViewModel = hiltViewModel()) {
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
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(8.dp)
                .underline(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                contentDescription = "User avatar with applied decorations",
                modifier = Modifier.size(256.dp),
                painter = BitmapPainter(state.avatar)
            )
        }

        val holders = state.holders.filter { it.owned }

        if (holders.isEmpty()) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                text = "No decorations owned"
            )
        } else {
            DecorationCards(
                actionImageVector = Icons.Outlined.TouchApp,
                actionPurchase = false,
                holders = holders,
            ) { model.apply(it.decoration) }
        }
    }
}

@Composable
fun DecorationScreen() {
    var screen by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxHeight()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.size(16.dp))

        PrimaryTabRow(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            divider = { VerticalDivider() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.9f)
                .clip(MaterialTheme.shapes.medium),
            selectedTabIndex = screen
        ) {
            screens()
                .forEachIndexed { index, (title, _) ->
                    Tab(
                        modifier = Modifier.height(32.dp),
                        onClick = { screen = index },
                        selected = screen == index,
                    ) {
                        Text(title)
                    }
                }
        }

        Spacer(modifier = Modifier.size(16.dp))

        screens()[screen]
            .second()
    }
}

@Composable
private fun Purchase(model: DecorationViewModel = hiltViewModel()) {
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

@Composable
private fun screens() = listOf<Pair<String, @Composable () -> Unit>>(
    "Purchase" to { Purchase() },
    "Owned" to { Apply() },
)