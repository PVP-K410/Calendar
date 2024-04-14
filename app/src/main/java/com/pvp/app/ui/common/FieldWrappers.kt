package com.pvp.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ErrorFieldWrapper(
    content: @Composable () -> Unit,
    messages: List<String> = emptyList(),
    style: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
) {
    content()

    if (messages.any()) {
        Column {
            for (message in messages) {
                Text(
                    text = message,
                    style = style
                )
            }
        }
    }
}

@Composable
fun LabelFieldWrapper(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    putBelow: Boolean = false,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    Column(modifier = modifier) {
        if (!putBelow) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = style,
                text = text,
                textAlign = textAlign
            )
        }

        content()

        if (putBelow) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = style,
                text = text,
                textAlign = textAlign
            )
        }
    }
}