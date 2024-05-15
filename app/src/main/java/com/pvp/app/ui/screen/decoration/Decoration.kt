@file:OptIn(ExperimentalLayoutApi::class)

package com.pvp.app.ui.screen.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pvp.app.ui.common.AsyncImage
import com.pvp.app.ui.common.FoldableContent
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.lighten

@Composable
private fun DecorationCard(
    holder: DecorationHolder,
    isClickable: Boolean = true,
    isStore: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer.darken(0.2f),
                shape = MaterialTheme.shapes.medium
            )
            .then(
                if (holder.applied)
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                else Modifier
            )
            .clickable(isClickable) { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                contentDescription = "Decoration ${holder.decoration.name} image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(
                        color = MaterialTheme.colorScheme.inverseOnSurface.lighten(),
                        shape = MaterialTheme.shapes.extraSmall
                    ),
                url = holder.decoration.imageRepresentativeUrl
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = holder.decoration.name,
                textAlign = TextAlign.Justify
            )

            if (!isStore) {
                return
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "${holder.decoration.price}"
                )

                Icon(
                    contentDescription = "Decoration ${holder.decoration.name} cost in points icon",
                    imageVector = Icons.Outlined.Stars
                )
            }
        }
    }
}

@Composable
fun DecorationCards(
    holders: List<DecorationHolder>,
    isClickable: Boolean,
    isStore: Boolean,
    onClick: (DecorationHolder) -> Unit
) {
    val holdersGrouped = remember(holders) { holders.groupBy { it.decoration.type } }

    holdersGrouped.onEachIndexed { index, (type, holdersGrouped) ->
        FoldableContent(
            content = {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    holdersGrouped.forEach { holder ->
                        DecorationCard(
                            holder = holder,
                            isClickable = isClickable,
                            isStore = isStore,
                            onClick = { onClick(holder) }
                        )
                    }
                }
            },
            header = type.toString()
        )

        if (index < holders.size - 1) {
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}