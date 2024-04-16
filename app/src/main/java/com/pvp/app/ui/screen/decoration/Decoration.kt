package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pvp.app.ui.common.AsyncImage
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.lighten
import com.pvp.app.ui.common.underline

@Composable
private fun BoxScope.ActionRowApply(
    actionImageVector: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .align(Alignment.BottomEnd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClick) {
            Icon(
                contentDescription = "Action icon",
                imageVector = actionImageVector
            )
        }
    }
}

@Composable
private fun BoxScope.ActionRowPurchase(
    actionImageVector: ImageVector,
    holder: DecorationHolder,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .align(Alignment.BottomEnd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!holder.owned) {
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = "${holder.decoration.price}"
            )

            Icon(
                contentDescription = "Cost in points icon",
                imageVector = Icons.Outlined.Stars
            )

            IconButton(onClick = onClick) {
                Icon(
                    contentDescription = "Action icon",
                    imageVector = actionImageVector
                )
            }
        } else {
            Text(
                color = Color.Red.darken(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                text = "Already Purchased"
            )

            Icon(
                contentDescription = "Already purchased icon",
                imageVector = Icons.Outlined.Check,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp),
                tint = Color.Red.darken()
            )
        }
    }
}

@Composable
private fun DecorationCard(
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
        Row(modifier = Modifier.padding(8.dp)) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .size(128.dp)
            ) {
                AsyncImage(
                    contentDescription = "Decoration ${holder.decoration.name} image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.lighten(0.2f),
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.inversePrimary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(16.dp),
                    url = holder.decoration.imageUrl
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = holder.decoration.name
                )

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = holder.decoration.description
                )
            }
        }

        if (actionPurchase) {
            ActionRowPurchase(
                actionImageVector = actionImageVector,
                holder = holder,
                onClick = onClick
            )
        } else {
            ActionRowApply(
                actionImageVector = actionImageVector,
                onClick = onClick
            )
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
        holders
            .groupBy { it.decoration.type }
            .forEach { (type, holdersGrouped) ->
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .underline(),
                    style = MaterialTheme.typography.titleMedium,
                    text = type.toString()
                )

                holdersGrouped.forEachIndexed { index, holder ->
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
}