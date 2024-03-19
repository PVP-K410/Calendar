package com.pvp.app.ui.screen.filters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pvp.app.ui.common.IconButtonWithDialog
import com.pvp.app.ui.common.underline

@Composable
fun FiltersItem(
    title: String,
    selectedFilters: List<String>,
    dialogTitle: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.underline()
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButtonWithDialog(
                        iconSize = 30.dp,
                        icon = Icons.Outlined.Edit,
                        iconDescription = "Edit Icon Button",
                        confirmButtonContent = {
                            Text("Edit")
                        },
                        dismissButtonContent = {
                            Text("Cancel")
                        },
                        dialogTitle = dialogTitle,
                        dialogContent = dialogContent,
                        onConfirmClick = onConfirmClick,
                        onDismiss = onDismiss
                    )
                }
            }

            FiltersBox(
                filters = selectedFilters
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersBox(
    title: String? = null,
    filters: List<String>,
    onClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                filters
                    .sorted()
                    .forEach { filter ->
                        Card(
                            modifier = Modifier
                                .padding(
                                    end = 4.dp,
                                    bottom = 4.dp
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onTertiary
                            ),
                            onClick = {
                                onClick(filter)
                            }
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        top = 12.dp,
                                        bottom = 12.dp,
                                        start = 6.dp,
                                        end = 6.dp
                                    ),
                                text = filter,
                                style = TextStyle(fontSize = 15.sp),
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun FiltersDialog(
    selectedFilters: List<String>,
    unselectedFilters: List<String>,
    onValueChange: (List<String>, List<String>) -> Unit
) {
    var selected by remember { mutableStateOf(selectedFilters) }
    var unselected by remember { mutableStateOf(unselectedFilters) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
    ) {
        FiltersBox(
            title = "Active filters:",
            filters = selected,
            onClick = { filter ->
                selected = selected.minus(filter)
                unselected = unselected.plus(filter)

                onValueChange(selected, unselected)
            }
        )

        FiltersBox(
            title = "Remaining filters:",
            filters = unselected,
            onClick = { filter ->
                unselected = unselected.minus(filter)
                selected = selected.plus(filter)

                onValueChange(selected, unselected)
            }
        )
    }
}