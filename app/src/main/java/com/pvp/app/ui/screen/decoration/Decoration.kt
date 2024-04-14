package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DecorationCard(
    actionImageVector: ImageVector,
    actionPurchase: Boolean,
    holder: DecorationHolder,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.titleLarge,
                text = holder.decoration.name
            )

            Text(
                modifier = Modifier.padding(8.dp),
                text = holder.decoration.description
            )

            if (actionPurchase || !holder.applied) {
                IconButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.End),
                    onClick = onClick
                ) {
                    Icon(
                        contentDescription = "Action icon",
                        imageVector = actionImageVector,
                    )
                }
            }
        }

        if (actionPurchase) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "Price: ${holder.decoration.price}"
                )

                Icon(
                    contentDescription = "Cost in points icon",
                    imageVector = Icons.Outlined.Stars
                )
            }
        }
    }
}

@Composable
fun DecorationCards(
    actionImageVector: ImageVector,
    actionPurchase: Boolean,
    holders: List<DecorationHolder>,
    onClick: (DecorationHolder) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        holders.forEachIndexed { index, holder ->
            DecorationCard(
                actionImageVector = actionImageVector,
                actionPurchase = actionPurchase,
                holder = holder,
                onClick = { onClick(holder) }
            )

            if (index < holders.size - 1) {
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}