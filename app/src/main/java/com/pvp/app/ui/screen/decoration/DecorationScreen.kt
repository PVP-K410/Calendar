package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.ui.common.Dialog
import com.pvp.app.ui.common.LocalShowSnackbar
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.TabSelector
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.orInDarkTheme

@Composable
private fun Apply(model: DecorationViewModel = hiltViewModel()) {
    val state by model.state.collectAsStateWithLifecycle()
    val holdersOwned by remember(state.holders) { mutableStateOf(state.holders.filter { it.owned }) }

    StateHandler(
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
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                        .darken(0.1f)
                        .orInDarkTheme(MaterialTheme.colorScheme.surfaceContainerHighest),
                    shape = CircleShape
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
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (holdersOwned.isEmpty()) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                text = stringResource(R.string.decorations_none_to_apply)
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

    val screens = listOf<Pair<String, @Composable () -> Unit>>(
        stringResource(R.string.decorations_store_title) to { Store() },
        stringResource(R.string.decorations_owned_title) to { Apply() },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .then(modifier)
            .padding(16.dp)
    ) {
        TabSelector(
            onSelect = { screen = it },
            tabs = screens.map { it.first },
        )

        Spacer(modifier = Modifier.size(16.dp))

        screens[screen].second()
    }
}

@Composable
private fun StateHandler(
    resetState: () -> Unit,
    state: DecorationScreenState
) {
    if (state is DecorationScreenState.Loading) {
        ProgressIndicatorWithinDialog()

        return
    }

    val showSnackbar = LocalShowSnackbar.current

    when (state) {
        is DecorationScreenState.Success -> {
            val localeSuccessApply = stringResource(R.string.decorations_success_apply)
            val localeSuccessPurchase = stringResource(R.string.decorations_success_purchase)
            val localeSuccessUnapply = stringResource(R.string.decorations_success_unapply)
            val localeSuccess = stringResource(R.string.decorations_success)

            LaunchedEffect(state) {
                when (state) {
                    is DecorationScreenState.Success.Apply -> showSnackbar(localeSuccessApply)
                    is DecorationScreenState.Success.Purchase -> showSnackbar(localeSuccessPurchase)
                    is DecorationScreenState.Success.Unapply -> showSnackbar(localeSuccessUnapply)
                    else -> showSnackbar(localeSuccess)
                }

                resetState()
            }
        }

        is DecorationScreenState.Error -> {
            val localeError = stringResource(R.string.decorations_error)
            val localeErrorOwn = stringResource(R.string.decorations_error_own)
            val localeErrorFunds = stringResource(R.string.decorations_error_funds)

            LaunchedEffect(state) {
                when (state) {
                    is DecorationScreenState.Error.AlreadyOwned -> showSnackbar(localeErrorOwn)
                    is DecorationScreenState.Error.InsufficientFunds -> showSnackbar(localeErrorFunds)
                    else -> showSnackbar(localeError)
                }

                resetState()
            }
        }

        else -> {}
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

    StateHandler(
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
                text = stringResource(
                    R.string.decorations_points,
                    state.user.points
                )
            )

            Icon(
                contentDescription = "Points indicator icon",
                imageVector = Icons.Outlined.Stars
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (holders.size == state.holders.size) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                text = stringResource(R.string.decorations_none_to_purchase)
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
                    text = stringResource(R.string.decorations_purchase_dialog_decoration)
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    text = item!!.decoration.name
                )

                Text(
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    text = stringResource(R.string.decorations_purchase_dialog_price)
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
        buttonContentConfirm = {
            Text(stringResource(R.string.action_purchase))
        },
        buttonContentDismiss = {
            Text(stringResource(R.string.action_cancel))
        },
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
        title = { Text(stringResource(R.string.decorations_purchase_dialog_title)) }
    )
}