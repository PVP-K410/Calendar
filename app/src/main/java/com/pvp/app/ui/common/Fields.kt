package com.pvp.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun InfoCustomField(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            label()

            value()
        }
    }
}

@Composable
fun InfoDateField(
    label: String,
    value: LocalDate
) = InfoTextField(
    label = label,
    value = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, EEEE"))
)

@Composable
fun InfoTextField(
    label: String,
    value: String
) = InfoCustomField(label = {
    Text(
        fontWeight = FontWeight.Bold,
        text = label
    )
}) { Text(text = value) }

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