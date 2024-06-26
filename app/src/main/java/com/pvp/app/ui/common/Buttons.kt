package com.pvp.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pvp.app.R
import java.time.LocalDateTime

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
    border: BorderStroke? = null,
    confirmationButtonContent: @Composable RowScope.() -> Unit = {
        val localeConfirm = stringResource(R.string.action_confirm)

        Text(
            localeConfirm,
            fontWeight = FontWeight.Bold
        )
    },
    confirmationDescription: @Composable () -> Unit = { },
    confirmationTitle: @Composable () -> Unit = { Text("Confirm to proceed") },
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit = { Text("Open Confirmation") },
    contentAlignment: Alignment = Alignment.Center,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.extraSmall
) {
    ButtonWithDialog(
        border = border,
        colors = colors,
        confirmButtonContent = confirmationButtonContent,
        content = content,
        contentAlignment = contentAlignment,
        dialogContent = confirmationDescription,
        dialogTitle = confirmationTitle,
        dismissButtonContent = {
            val localeCancel = stringResource(R.string.action_cancel)

            Text(localeCancel)
        },
        modifier = modifier,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        shape = shape
    )
}

@Composable
fun ButtonWithDialog(
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit = { Text("Open Dialog") },
    contentAlignment: Alignment = Alignment.TopStart,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    confirmButtonContent: @Composable RowScope.() -> Unit = {
        val localeConfirm = stringResource(R.string.action_confirm)

        Text(
            localeConfirm,
            fontWeight = FontWeight.Bold
        )
    },
    dismissButtonContent: @Composable RowScope.() -> Unit = {
        val localeCancel = stringResource(R.string.action_cancel)

        Text(localeCancel)
    },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.extraSmall,
    showConfirmButton: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = contentAlignment,
        modifier = modifier
    ) {
        Button(
            border = border,
            colors = colors,
            content = content,
            contentPadding = contentPadding,
            onClick = { showDialog = true },
            shape = shape
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
        showConfirmButton = showConfirmButton,
        title = dialogTitle
    )
}

@Composable
fun IconButtonConfirm(
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    confirmationButtonContent: @Composable RowScope.() -> Unit = {
        val localeConfirm = stringResource(R.string.action_confirm)

        Text(localeConfirm)
    },
    confirmationButtonEnabled: Boolean = true,
    confirmationDescription: @Composable () -> Unit = { },
    confirmationTitle: @Composable () -> Unit = { Text("Confirm to proceed") },
    icon: ImageVector,
    iconDescription: String? = null,
    iconSize: Dp = 20.dp,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    val localeCancel = stringResource(R.string.action_cancel)

    IconButtonWithDialog(
        colors = colors,
        confirmButtonContent = confirmationButtonContent,
        confirmButtonEnabled = confirmationButtonEnabled,
        dialogContent = confirmationDescription,
        dialogTitle = confirmationTitle,
        dismissButtonContent = { Text(localeCancel) },
        icon = icon,
        iconDescription = iconDescription,
        iconSize = iconSize,
        modifier = modifier,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun IconButtonWithDatePickerDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconSize: Dp = 20.dp,
    iconDescription: String? = null,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    onDateSelected: (LocalDateTime) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            colors = colors,
            onClick = { showDialog = true },
            modifier = Modifier.size(iconSize)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }

    DatePickerDialog(
        showPicker = showDialog,
        onDismiss = {
            showDialog = false
        },
        onDateSelected = { selectedDate ->
            onDateSelected(selectedDate)

            showDialog = false
        }
    )
}

@Composable
fun IconButtonWithDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconSize: Dp = 20.dp,
    iconDescription: String? = null,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    confirmButtonContent: @Composable RowScope.() -> Unit = {
        val localeConfirm = stringResource(R.string.action_confirm)

        Text(localeConfirm)
    },
    confirmButtonEnabled: Boolean = true,
    dismissButtonContent: @Composable RowScope.() -> Unit = {
        val localeCancel = stringResource(R.string.action_cancel)

        Text(localeCancel)
    },
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
            colors = colors,
            onClick = { showDialog = true },
            modifier = Modifier.size(iconSize)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }

    Dialog(
        buttonContentConfirm = confirmButtonContent,
        buttonContentDismiss = dismissButtonContent,
        buttonEnabledConfirm = confirmButtonEnabled,
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
    show: Boolean,
    showConfirmButton: Boolean = true
) {
    if (!show) {
        return
    }

    AlertDialog(
        confirmButton = {
            if (showConfirmButton) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Button(
                        colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.surface),
                        content = buttonContentConfirm,
                        onClick = onConfirm,
                        shape = MaterialTheme.shapes.extraLarge,
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        dismissButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                OutlinedButton(
                    border = null,
                    content = buttonContentDismiss,
                    onClick = onDismiss,
                    shape = MaterialTheme.shapes.extraLarge
                )
            }
        },
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraSmall,
        text = content,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = title,
        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}