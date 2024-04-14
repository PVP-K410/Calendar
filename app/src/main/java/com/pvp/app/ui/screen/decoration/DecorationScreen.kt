package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun DecorationScreen() {
    var screen by remember { mutableIntStateOf(0) }

    fun changePage() {
        screen = when (screen) {
            0 -> 1
            else -> 0
        }
    }

    @Composable
    fun fontStyle(screenTarget: Int): TextStyle = MaterialTheme.typography.titleLarge.copy(
        textDecoration = if (screen == screenTarget) TextDecoration.Underline else TextDecoration.None,
        fontWeight = if (screen == screenTarget) FontWeight.Bold else FontWeight.Normal
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f)
                .height(32.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.medium
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
                    .clickable { changePage() }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    style = fontStyle(screenTarget = 0),
                    text = "Shop"
                )
            }

            VerticalDivider()

            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
                    .clickable { changePage() }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    style = fontStyle(screenTarget = 1),
                    text = "Purchased"
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        when (screen) {
            0 -> DecorationPurchase()
            1 -> DecorationSelect()
        }
    }
}