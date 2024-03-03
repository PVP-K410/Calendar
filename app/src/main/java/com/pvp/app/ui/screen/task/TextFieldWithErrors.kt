package com.yourapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType


/**
 * @param value text field input text
 * @param onValueChange (optional) callback triggered when text inside text field is updated
 * @param validationPolicies (optional) how text inside should be validated,
 *          should be specified via InputValidator
 * @param label label displayed inside text container
 * @param keyboardOptions (optional) software keyboard options that contains configuration such as KeyboardType and ImeAction.
 * @param modifier (optional) the Modifier applies to the text field
 */
@Composable
fun TextFieldWithErrors(
    value: String,
    onValueChange: (String, List<String>) -> Unit = { _, _ -> },
    validationPolicies: (String) -> List<String> = { listOf<String>() },
    label: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    modifier: Modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxWidth()
) {
    var errors by remember { mutableStateOf(emptyList<String>()) }
    var input by remember { mutableStateOf(value) }

    ErrorField(
        content = {
            TextField(
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
                        Icon(Icons.Filled.Error, "Error")
                    }
                },
                keyboardOptions = keyboardOptions,
            )
        }, messages = errors
    )
}

@Composable
fun ErrorField(
    content: @Composable () -> Unit,
    messages: Collection<String> = emptyList(),
    style: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
) {
    content()

    if (messages.any()) {
        Column {
            for (message in messages) {
                Text(
                    text = message, style = style
                )
            }
        }
    }
}
