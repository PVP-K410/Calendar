@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.ui.common.Dialog
import com.pvp.app.ui.screen.decoration.DecorationScreenState.Companion.ScreenStateHandler

@Composable
private fun Apply(model: DecorationViewModel = hiltViewModel()) {
    val state by model.state.collectAsStateWithLifecycle()
    val holdersOwned by remember(state.holders) { mutableStateOf(state.holders.filter { it.owned }) }

    ScreenStateHandler(
        resetState = model::resetScreenState,
        state = state.state
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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                contentDescription = "User avatar with applied decorations",
                modifier = Modifier.size(256.dp),
                painter = BitmapPainter(state.avatar)
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(16.dp)
        )

        if (holdersOwned.isEmpty()) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                text = "No decorations owned"
            )
        } else {
            DecorationCards(
                isStore = false,
                holders = state.holders.filter { h -> h in holdersOwned },
                isClickable = state.state is DecorationScreenState.NoOperation
            ) { model.apply(it.decoration) }
        }
    }
}

@Composable
fun DecorationScreen(modifier: Modifier) {
    var screen by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .then(modifier)
            .background(MaterialTheme.colorScheme.background)
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
                    ) { Text(title) }
                }
        }

        Spacer(modifier = Modifier.size(16.dp))

        screens()[screen]
            .second()
    }
}

@Composable
private fun Store(model: DecorationViewModel = hiltViewModel()) {
    var item by remember { mutableStateOf<DecorationHolder?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val state by model.state.collectAsStateWithLifecycle()

    val holders by remember(state.holders) {
        mutableStateOf(state.holders.filter { it.owned || it.decoration.price < 0 })
    }

    ScreenStateHandler(
        resetState = model::resetScreenState,
        state = state.state
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 4.dp),
                text = "Your points ${state.user.points}"
            )

            Icon(
                contentDescription = "Points indicator icon",
                imageVector = Icons.Outlined.Stars
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(16.dp)
        )

        if (holders.size == state.holders.size) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                text = "No decorations to purchase"
            )
        } else {
            DecorationCards(
                isStore = true,
                holders = state.holders.filter { h -> h !in holders },
                isClickable = state.state is DecorationScreenState.NoOperation
            ) {
                // Order of operations is important here, else NPE will occur
                item = it
                showDialog = true
            }
        }
    }

    Dialog(
        content = {
            Column {
                Text(
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    text = "Decoration"
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    text = item!!.decoration.name
                )

                Text(
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    text = "Price"
                )

                Row {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        text = "${item!!.decoration.price}"
                    )

                    Icon(
                        contentDescription = "Decoration ${item!!.decoration.name} cost in points icon",
                        imageVector = Icons.Outlined.Stars
                    )
                }
            }
        },
        buttonContentConfirm = { Text("Purchase") },
        buttonContentDismiss = { Text("Cancel") },
        onConfirm = {
            model.purchase(item!!.decoration)

            showDialog = false
            item = null
        },
        onDismiss = {
            showDialog = false
            item = null
        },
        show = showDialog,
        title = { Text("Confirm purchase") }
    )
}

@Composable
private fun screens() = listOf<Pair<String, @Composable () -> Unit>>(
    "Store" to { Store() },
    "Owned" to { Apply() },
)