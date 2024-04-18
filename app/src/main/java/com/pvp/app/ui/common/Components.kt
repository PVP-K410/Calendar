package com.pvp.app.ui.common

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.size.Size
import coil.transform.Transformation
import com.pvp.app.ui.common.ImageUtil.requestImage

@Composable
fun AsyncImage(
    contentDescription: String? = null,
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    size: Size = Size.ORIGINAL,
    transformations: List<Transformation> = emptyList(),
    url: String
) {
    SubcomposeAsyncImage(
        contentDescription = contentDescription,
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
                    contentDescription = "Error indicator for image loading: $url",
                    imageVector = Icons.Outlined.ErrorOutline,
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun Dialog(
    content: @Composable () -> Unit,
    buttonContentConfirm: @Composable RowScope.() -> Unit,
    buttonContentDismiss: @Composable RowScope.() -> Unit,
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
                    onClick = onConfirm
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        dismissButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                OutlinedButton(
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

@Composable
fun FoldableContent(
    content: @Composable ColumnScope.() -> Unit,
    header: @Composable RowScope.() -> Unit,
    isFoldedInitially: Boolean = false
) {
    var folded by remember { mutableStateOf(isFoldedInitially) }

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
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
    header: String,
    isFoldedInitially: Boolean = false
) = FoldableContent(
    content = content,
    header = {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = header
        )
    },
    isFoldedInitially = isFoldedInitially
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
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}