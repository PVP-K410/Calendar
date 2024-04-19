package com.pvp.app.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressIndicator(
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = indicatorColor,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}

@Composable
fun Experience(
    experience: Int,
    experienceRequired: Int,
    level: Int,
    paddingEnd: Dp = 30.dp,
    paddingStart: Dp = 30.dp,
    fontSize: Int = 18,
    fontWeight: FontWeight = FontWeight.Bold,
    height: Dp = 32.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    progressTextStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = paddingEnd,
                start = paddingStart
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            text = "Level $level",
            style = textStyle
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            var target by remember { mutableFloatStateOf(0f) }

            val progress by animateFloatAsState(
                animationSpec = tween(durationMillis = 1000),
                label = "ExperienceProgressAnimation",
                targetValue = target,
            )

            LaunchedEffect(Unit) {
                target = experience / experienceRequired.toFloat()
            }

            LinearProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                progress = { progress },
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
            )

            Text(
                style = progressTextStyle,
                text = "$experience / $experienceRequired Exp",
                textAlign = TextAlign.Center
            )
        }
    }
}