package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pvp.app.model.Decoration
import com.pvp.app.model.Reward
import com.pvp.app.model.Streak
import com.pvp.app.ui.common.AsyncImage
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.lighten

@Composable
private fun DecorationCard(decoration: Decoration) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium,
            text = "${decoration.name} decoration!"
        )

        AsyncImage(
            contentDescription = "Decoration ${decoration.name} image",
            modifier = Modifier
                .size(96.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface.lighten(),
                    shape = MaterialTheme.shapes.extraSmall
                ),
            url = decoration.imageRepresentativeUrl
        )
    }
}

@Composable
fun RewardDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    reward: Reward,
    streak: Streak
) {
    if (!isOpen) {
        return
    }

    Dialog(onDismissRequest = onClose) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 20.sp,
                text = "Your daily login streak increased!"
            )

            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Star icon",
                    modifier = Modifier.size(95.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    text = streak.value.toString(),
                    textAlign = TextAlign.Center
                )
            }

            Text(
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 20.sp,
                text = "You received"
            )

            Spacer(modifier = Modifier.padding(6.dp))

            if (reward.points > 0) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(3.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = "${reward.points}"
                    )

                    Icon(
                        contentDescription = "Points indicator icon",
                        imageVector = Icons.Outlined.Stars
                    )
                }
            }

            if (reward.experience > 0) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = "${reward.experience} xp"
                )
            }

            reward.decoration?.let {
                DecorationCard(decoration = it)

                Spacer(modifier = Modifier.padding(8.dp))
            }


            Button(onClick = { onClose() }) {
                Text("Claim")
            }
        }
    }
}