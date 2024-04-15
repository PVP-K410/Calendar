package com.pvp.app.ui.screen.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun Day(
    date: LocalDate = LocalDate.MIN,
    name: String = "Day",
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .size(
                    height = 180.dp,
                    width = 200.dp
                )
                .border(
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        onClick = onClick
                    )
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        text = name,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        fontSize = 50.sp,
                        text = date.dayOfMonth.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun DayCard(
    date: LocalDate,
    day: String,
    onClick: () -> Unit,
    page: Int,
    pageIndex: Int
) {
    val scale by animateFloatAsState(
        targetValue = if (pageIndex == page) 1f else 0.8f,
        animationSpec = spring(stiffness = 500f),
        label = "DayCardAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                alpha = if (pageIndex == page) 1f else 0.5f,
                scaleX = scale,
                scaleY = scale
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Day(
            date = date,
            name = day,
            onClick = onClick
        )
    }
}