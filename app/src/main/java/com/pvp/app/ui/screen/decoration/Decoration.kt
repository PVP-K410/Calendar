package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.lighten
import com.pvp.app.ui.common.underline

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
        Row(modifier = Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    contentDescription = "Decoration ${holder.decoration.name} image",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    painter = BitmapPainter(holder.image),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f),
                horizontalAlignment = Alignment.End
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

        Row(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.BottomEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (actionPurchase) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "${holder.decoration.price}"
                )

                Icon(
                    contentDescription = "Cost in points icon",
                    imageVector = Icons.Outlined.Stars
                )
            }

            IconButton(onClick = onClick) {
                Icon(
                    contentDescription = "Action icon",
                    modifier = Modifier.then(
                        when {
                            actionPurchase -> Modifier.underline(
                                if (holder.owned) Color.Red.darken() else Color.Green.lighten()
                            )

                            else -> Modifier
                        }
                    ),
                    imageVector = actionImageVector
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