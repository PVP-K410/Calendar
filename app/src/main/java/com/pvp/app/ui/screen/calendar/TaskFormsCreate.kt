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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Composable
private fun ColumnScope.TaskTypeSelector(onSelect: (KClass<out Task>) -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    PrimaryTabRow(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        divider = {},
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        selectedTabIndex = selectedTabIndex
    ) {
        mapOf(
            GeneralTask::class to "General",
            CustomMealTask::class to "Meal",
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
                        color = MaterialTheme.colorScheme.inverseSurface,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium,
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
            var target by remember { mutableStateOf(GeneralTask::class as KClass<out Task>) }

            TaskTypeSelector { targetNew -> target = targetNew }

            Spacer(modifier = Modifier.size(16.dp))

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