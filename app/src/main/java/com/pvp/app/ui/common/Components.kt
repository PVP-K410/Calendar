package com.pvp.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
        CircularProgressIndicator()
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

@Composable
fun ButtonWithDialog(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    mainButtonContent: @Composable RowScope.() -> Unit = { Text("Open Dialog") },
    confirmButtonContent: @Composable RowScope.() -> Unit = { Text("Confirm") },
    dismissButtonContent: @Composable RowScope.() -> Unit = { Text("Dismiss") },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onConfirmClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        androidx.compose.material3.Button(
            content = mainButtonContent,
            onClick = {
                showDialog = true
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            title = dialogTitle,
            text = dialogContent,
            onDismissRequest = {
                onDismiss()

                showDialog = false
            },
            confirmButton = {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    androidx.compose.material3.Button(
                        content = confirmButtonContent,
                        onClick = {
                            onConfirmClick()

                            showDialog = false
                        }
                    )
                }
            },
            dismissButton = {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    androidx.compose.material3.Button(
                        content = dismissButtonContent,
                        onClick = {
                            onDismiss()

                            showDialog = false
                        },
                    )
                }
            }
        )
    }
}

@Composable
fun IconButtonWithDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Edit,
    iconSize: Dp = 20.dp,
    confirmButtonContent: @Composable RowScope.() -> Unit = { Text("Confirm") },
    dismissButtonContent: @Composable RowScope.() -> Unit = { Text("Dismiss") },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onConfirmClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.size(iconSize)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            title = dialogTitle,
            text = dialogContent,
            onDismissRequest = {
                onDismiss()

                showDialog = false
            },
            confirmButton = {
                Box(
                    contentAlignment = Alignment.BottomStart
                ) {
                    androidx.compose.material3.Button(
                        content = confirmButtonContent,
                        onClick = {
                            onConfirmClick()

                            showDialog = false
                        }
                    )
                }

            },
            dismissButton = {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    androidx.compose.material3.Button(
                        content = dismissButtonContent,
                        onClick = {
                            onDismiss()

                            showDialog = false
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun UserInfoItem(
    label: String,
    value: String,
    dialogTitle: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.underline()
                )

                Text(
                    text = value,
                    fontStyle = FontStyle.Italic
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButtonWithDialog(
                    iconSize = 30.dp,
                    confirmButtonContent = {
                        Text("Edit")
                    },
                    dismissButtonContent = {
                        Text("Cancel")
                    },
                    dialogTitle = dialogTitle,
                    dialogContent = dialogContent,
                    onConfirmClick = onConfirmClick,
                    onDismiss = onDismiss
                )
            }
        }
    }
}