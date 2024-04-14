@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DecorationScreen() {
    var screen by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.size(16.dp))

        SecondaryTabRow(
            containerColor = Color.Transparent,
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
private fun screens() = listOf<Pair<String, @Composable () -> Unit>>(
    "Purchase" to { DecorationPurchase() },
    "Owned" to { DecorationSelect() },
)