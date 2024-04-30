package com.pvp.app.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

@Composable
fun RouteIcon(
    imageVector: ImageVector,
    resourceId: Int
) {
    Icon(
        contentDescription = "${stringResource(resourceId)} route button icon",
        imageVector = imageVector
    )
}

@Composable
fun RouteTitle(title: String) {
    Text(
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
        text = title
    )
}