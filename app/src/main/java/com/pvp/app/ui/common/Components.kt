@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.pvp.app.ui.common

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.size.Size
import coil.transform.Transformation
import com.pvp.app.R
import com.pvp.app.ui.common.ImageUtil.requestImage
import kotlinx.coroutines.launch

@Composable
fun AsyncImage(
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    size: Size = Size.ORIGINAL,
    transformations: List<Transformation> = emptyList(),
    url: String
) {
    SubcomposeAsyncImage(
        contentDescription = contentDescription,
        contentScale = contentScale,
        model = requestImage(
            context = context,
            size = size,
            transformations = transformations,
            url = url
        ),
        modifier = modifier
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> ProgressIndicator()
            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()

            else -> {
                Icon(
                    contentDescription = "Error indicator for failed image load",
                    imageVector = Icons.Outlined.ErrorOutline,
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun CenteredSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .padding(16.dp),
                action = {
                    snackbarData.visuals.actionLabel?.let { actionLabel ->
                        TextButton(onClick = { snackbarData.performAction() }) {
                            Text(text = actionLabel)
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.logo),
                        contentDescription = "App logo",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                    )

                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = snackbarData.visuals.message,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

@Composable
fun Dialog(
    buttonContentConfirm: @Composable RowScope.() -> Unit,
    buttonContentDismiss: @Composable RowScope.() -> Unit,
    buttonEnabledConfirm: Boolean = true,
    content: @Composable () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    title: @Composable () -> Unit
) {
    if (!show) {
        return
    }

    AlertDialog(
        confirmButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                Button(
                    content = buttonContentConfirm,
                    onClick = onConfirm,
                    enabled = buttonEnabledConfirm,
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.surface)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        dismissButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                OutlinedButton(
                    content = buttonContentDismiss,
                    onClick = onDismiss,
                    shape = MaterialTheme.shapes.extraLarge,
                    border = null
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

@Composable
fun FoldableContent(
    content: @Composable ColumnScope.() -> Unit,
    darken: Boolean = true,
    header: @Composable RowScope.() -> Unit,
    isFoldedInitially: Boolean = false,
    modifier: Modifier = Modifier
) {
    var folded by remember { mutableStateOf(isFoldedInitially) }

    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer.darken(if (darken) 0.2f else 0f),
                    shape = MaterialTheme.shapes.medium
                )
                .clickable { folded = !folded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            header()

            Icon(
                contentDescription = "Foldable content icon",
                imageVector = if (folded) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp
            )
        }

        AnimatedVisibility(visible = !folded) { content() }
    }
}

@Composable
fun FoldableContent(
    content: @Composable ColumnScope.() -> Unit,
    darken: Boolean = true,
    header: String,
    isFoldedInitially: Boolean = false,
    modifier: Modifier = Modifier
) = FoldableContent(
    content = content,
    darken = darken,
    header = {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = header
        )
    },
    isFoldedInitially = isFoldedInitially,
    modifier = modifier
)

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
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

@Composable
fun ProgressIndicatorWithinDialog(
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier.fillMaxSize()
) = androidx.compose.ui.window.Dialog(onDismissRequest = {}) {
    ProgressIndicator(
        indicatorColor,
        modifier
    )
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
            text = stringResource(R.string.global_level) + " $level",
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
                text = "$experience / $experienceRequired " + stringResource(R.string.global_experience),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoTooltip(
    tooltipText: String,
    iconSize: Dp = 24.dp
) = InfoTooltip(
    iconSize = iconSize,
    tooltip = {
        Text(
            text = tooltipText,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    shape = MaterialTheme.shapes.medium,
                )
                .height(40.dp)
                .padding(8.dp)
                .wrapContentSize(Alignment.Center),
            color = Color.White
        )
    }
)

@Composable
fun InfoTooltip(
    iconSize: Dp = 24.dp,
    iconTint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
    tooltip: @Composable () -> Unit
) {
    val tooltipState = rememberBasicTooltipState()
    val scope = rememberCoroutineScope()

    BasicTooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = tooltip,
        state = tooltipState
    ) {
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = { scope.launch { tooltipState.show() } },
        ) {
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .padding(horizontal = 4.dp),
                imageVector = Icons.Outlined.Info,
                contentDescription = "Additional information",
                tint = iconTint
            )
        }
    }
}

@Composable
fun TabSelector(
    onSelect: (Int) -> Unit,
    tab: Int = 0,
    tabs: List<String>,
    withShadow: Boolean = true
) {
    var tab by remember(tab) { mutableIntStateOf(tab) }

    Row(
        modifier = Modifier.then(
            if (withShadow) Modifier.shadow(
                elevation = 6.dp,
                shape = MaterialTheme.shapes.medium
            ) else Modifier
        )
    ) {
        PrimaryTabRow(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            selectedTabIndex = tab
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.height(32.dp),
                    onClick = {
                        tab = index

                        onSelect(index)
                    },
                    selected = tab == index
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        fontWeight = if (tab == index) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium,
                        text = title
                    )
                }
            }
        }
    }
}