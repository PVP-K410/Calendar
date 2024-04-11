package com.pvp.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
                    onValueChange(
                        it,
                        errors
                    )
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

@Composable
fun EditableInfoItem(
    dialogContent: @Composable () -> Unit,
    dialogTitle: @Composable () -> Unit,
    label: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    value: String,
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )

            Text(text = value)
        }

        IconButtonConfirm(
            confirmationButtonContent = {
                Text("Save")
            },
            confirmationDescription = dialogContent,
            confirmationTitle = dialogTitle,
            icon = Icons.Outlined.Edit,
            iconSize = 30.dp,
            iconDescription = "Edit Icon Button",
            modifier = Modifier.align(Alignment.TopEnd),
            onConfirm = { onConfirm(value) },
            onDismiss = onDismiss
        )
    }
}