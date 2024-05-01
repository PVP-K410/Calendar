@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Composable
private fun ColumnScope.TaskTypeSelector(onSelect: (KClass<out Task>) -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    PrimaryTabRow(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .align(Alignment.CenterHorizontally)
            .clip(MaterialTheme.shapes.medium),
        selectedTabIndex = selectedTabIndex
    ) {
        mapOf(
            Task::class to "General",
            MealTask::class to "Meal",
            SportTask::class to "Sport"
        )
            .onEachIndexed { index, (taskClass, taskText) ->
                Tab(
                    modifier = Modifier.height(32.dp),
                    onClick = {
                        selectedTabIndex = index

                        onSelect(taskClass)
                    },
                    selected = selectedTabIndex == index
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = taskText
                    )
                }
            }
    }
}

@Composable
fun TaskCreateSheet(
    date: LocalDateTime? = null,
    onClose: () -> Unit,
    isOpen: Boolean,
    shouldCloseOnSubmit: Boolean
) {
    if (!isOpen) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState(true)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
        ) {
            var target by remember { mutableStateOf(Task::class as KClass<out Task>) }

            TaskTypeSelector { targetNew -> target = targetNew }

            Spacer(modifier = Modifier.size(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.size(8.dp))

            TaskCommonForm(
                date = date,
                targetClass = target,
                onClose = {
                    if (shouldCloseOnSubmit) {
                        onClose()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
    }
}