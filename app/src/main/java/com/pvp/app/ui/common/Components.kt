package com.pvp.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun ErrorFieldWrapper(
    content: @Composable () -> Unit,
    messages: Collection<String> = emptyList(),
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
    style: TextStyle = MaterialTheme.typography.titleMedium,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    Column(
        modifier = modifier,
    ) {
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

@Composable
fun ProgressIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}

/**
 * @param keyboardOptions (optional) software keyboard options that contains configuration such
 * as KeyboardType and ImeAction.
 * @param label label displayed inside text container
 * @param modifier (optional) the Modifier applies to the text field
 * @param onValueChange (optional) callback triggered when text inside text field is updated
 * @param validationPolicies (optional) how text inside should be validated, should be specified
 * via InputValidator
 * @param value text field input text
 */
@Composable
fun TextField(
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    label: String,
    modifier: Modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxWidth(),
    onValueChange: (String, List<String>) -> Unit = { _, _ -> },
    validationPolicies: (String) -> List<String> = { listOf() },
    value: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        validationPolicies = validationPolicies,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

/**
 * @param keyboardOptions (optional) software keyboard options that contains configuration such
 * as KeyboardType and ImeAction.
 * @param label label displayed inside text container
 * @param modifier (optional) the Modifier applies to the text field
 * @param onValueChange (optional) callback triggered when text inside text field is updated
 * @param validationPolicies (optional) how text inside should be validated, should be specified
 * via InputValidator
 * @param value text field input text
 */
@Composable
fun TextField(
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxWidth(),
    onValueChange: (String, List<String>) -> Unit = { _, _ -> },
    validationPolicies: (String) -> List<String> = { listOf() },
    value: String
) {
    var errors by remember { mutableStateOf(emptyList<String>()) }
    var input by remember { mutableStateOf(value) }

    ErrorFieldWrapper(
        content = {
            androidx.compose.material3.TextField(
                value = input,
                onValueChange = {
                    input = it
                    errors = validationPolicies(it)
                    onValueChange(it, errors)
                },
                label = label,
                modifier = modifier,
                trailingIcon = {
                    if (errors.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Outlined.Error,
                            contentDescription = "Error"
                        )
                    }
                },
                keyboardOptions = keyboardOptions,
            )
        },
        messages = errors
    )
}