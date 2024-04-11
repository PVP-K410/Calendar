package com.pvp.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
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
    androidx.compose.material3.Button(
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
fun ButtonConfirm(
    confirmationButtonContent: @Composable RowScope.() -> Unit = { Text("Proceed") },
    confirmationDescription: @Composable () -> Unit = { },
    confirmationTitle: @Composable () -> Unit = { Text("Confirm to proceed") },
    buttonContent: @Composable RowScope.() -> Unit = { Text("Open Confirmation") },
    buttonContentAlignment: Alignment = Alignment.Center,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    ButtonWithDialog(
        confirmButtonContent = confirmationButtonContent,
        content = buttonContent,
        contentAlignment = buttonContentAlignment,
        dialogContent = confirmationDescription,
        dialogTitle = confirmationTitle,
        dismissButtonContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    contentDescription = "Cancel button icon",
                    imageVector = Icons.AutoMirrored.Outlined.Undo
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Cancel")
            }
        },
        modifier = modifier,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun ButtonWithDialog(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit = { Text("Open Dialog") },
    contentAlignment: Alignment = Alignment.TopStart,
    confirmButtonContent: @Composable RowScope.() -> Unit = { Text("Confirm") },
    dismissButtonContent: @Composable RowScope.() -> Unit = { Text("Dismiss") },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = contentAlignment,
        modifier = modifier
    ) {
        Button(
            content = content,
            onClick = { showDialog = true }
        )
    }

    Dialog(
        buttonContentConfirm = confirmButtonContent,
        buttonContentDismiss = dismissButtonContent,
        content = dialogContent,
        onConfirm = {
            onConfirm()

            showDialog = false
        },
        onDismiss = {
            onDismiss()

            showDialog = false
        },
        show = showDialog,
        title = dialogTitle
    )
}

@Composable
fun IconButtonWithDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconSize: Dp = 20.dp,
    iconDescription: String? = null,
    confirmButtonContent: @Composable RowScope.() -> Unit = { Text("Confirm") },
    dismissButtonContent: @Composable RowScope.() -> Unit = { Text("Dismiss") },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onConfirm: () -> Unit = {},
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
                contentDescription = iconDescription
            )
        }
    }

    Dialog(
        buttonContentConfirm = confirmButtonContent,
        buttonContentDismiss = dismissButtonContent,
        content = dialogContent,
        onConfirm = {
            onConfirm()

            showDialog = false
        },
        onDismiss = {
            onDismiss()

            showDialog = false
        },
        show = showDialog,
        title = dialogTitle
    )
}

@Composable
private fun Dialog(
    content: @Composable () -> Unit,
    title: @Composable () -> Unit,
    buttonContentConfirm: @Composable RowScope.() -> Unit,
    buttonContentDismiss: @Composable RowScope.() -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean
) {
    if (!show) {
        return
    }

    AlertDialog(
        confirmButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                Button(
                    border = BorderStroke(
                        1.dp,
                        Color.Red
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    content = buttonContentConfirm,
                    onClick = onConfirm
                )
            }
        },
        dismissButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                Button(
                    content = buttonContentDismiss,
                    onClick = onDismiss,
                    shape = MaterialTheme.shapes.extraLarge
                )
            }
        },
        onDismissRequest = onDismiss,
        text = content,
        title = title
    )
}