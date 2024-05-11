@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalendarScreen(modifier: Modifier) {
    var isOpen by remember { mutableStateOf(false) }
    val toggle = remember { { isOpen = !isOpen } }

    val state = rememberPagerState(
        pageCount = { 2 },
        initialPage = 1
    )

    Box {
        VerticalPager(state = state) { page ->
            when (page) {
                0 -> CalendarMonthlyScreen(modifier = modifier)
                1 -> CalendarWeeklyScreen(modifier = modifier)
            }
        }

        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = toggle,
            shape = CircleShape
        ) {
            Icon(
                contentDescription = "Create task",
                imageVector = Icons.Outlined.Add,
                tint = MaterialTheme.colorScheme.surface
            )
        }

        TaskCreateSheet(
            isOpen = isOpen,
            onClose = toggle
        )
    }
}