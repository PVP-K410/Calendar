package com.pvp.app.ui.common

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
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
    AsyncImage(
        contentDescription = contentDescription,
        model = requestImage(
            context = context,
            size = size,
            transformations = transformations,
            url = url
        ),
        modifier = modifier
    )
}

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