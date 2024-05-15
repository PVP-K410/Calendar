package com.pvp.app.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextError(
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    text: String = "Invalid input"
) {
    if (enabled && text.isNotEmpty()) {
        Text(
            modifier = modifier.padding(top = 8.dp),
            text = text,
            color = MaterialTheme.colorScheme.error
        )
    }
}