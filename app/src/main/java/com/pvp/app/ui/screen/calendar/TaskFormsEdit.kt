@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pvp.app.model.Task

@Composable
fun TaskEditSheet(
    task: Task,
    onDialogClose: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDialogClose,
        sheetState = rememberModalBottomSheetState(true)
    ) {
        TaskEditForm(
            task = task,
            onDialogClose = onDialogClose
        )

        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun TaskEditForm(
    task: Task,
    onDialogClose: () -> Unit
) {
    TaskCommonForm(
        task = task,
        onDialogClose = onDialogClose
    )
}