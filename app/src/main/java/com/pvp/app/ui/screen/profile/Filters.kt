package com.pvp.app.ui.screen.profile

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pvp.app.R
import com.pvp.app.ui.common.IconButtonWithDialog

@Composable
fun FiltersItem(
    title: String,
    selectedFilters: List<String>,
    dialogTitle: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
    textOtherEmpty: String,
    textSelectedEmpty: String
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
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
                        fontWeight = FontWeight.Bold,
                        text = title
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
                            val localeSave = stringResource(R.string.action_save)

                            Text(
                                localeSave,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        dismissButtonContent = {
                            val localeCancel = stringResource(R.string.action_cancel)

                            Text(localeCancel)
                        },
                        dialogTitle = dialogTitle,
                        dialogContent = dialogContent,
                        onConfirm = onConfirmClick,
                        onDismiss = onDismiss
                    )
                }
            }

            FiltersBox(
                filters = selectedFilters,
                cardsClickable = false,
                textOtherEmpty = textOtherEmpty,
                textSelectedEmpty = textSelectedEmpty
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersBox(
    boxTitle: String? = null,
    filters: List<String>,
    isSelected: Boolean? = null,
    onClick: (String) -> Unit = {},
    textOtherEmpty: String? = null,
    textSelectedEmpty: String? = null,
    emptyBoxText: String? = null,
    cardsClickable: Boolean = true
) {
    require(emptyBoxText != null || textOtherEmpty != null && textSelectedEmpty != null) {
        "Either emptyBoxText or textOtherEmpty and textSelectedEmpty must be provided."
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (boxTitle != null) {
                Text(
                    text = boxTitle,
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (filters.isEmpty()) {
                Text(
                    text = emptyBoxText
                        ?: if (isSelected == false) textOtherEmpty!! else textSelectedEmpty!!,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.padding(8.dp)
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                filters
                    .sorted()
                    .forEach { filter ->
                        Box(
                            modifier = Modifier
                                .padding(
                                    end = 4.dp,
                                    bottom = 4.dp
                                )
                        ) {
                            Card(
                                enabled = cardsClickable,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp
                                ),
                                shape = MaterialTheme.shapes.medium,
                                onClick = {
                                    onClick(filter)
                                },
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

                            if (isSelected != null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(
                                            end = 0.dp,
                                            top = 0.dp
                                        )
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .size(16.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected == true) Icons.Outlined.Remove else Icons.Outlined.Add,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                }
                            }
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
    onValueChange: (List<String>, List<String>) -> Unit,
    titleSelected: String,
    titleOther: String,
    textSelectedEmpty: String,
    textOtherEmpty: String
) {
    var selected by remember { mutableStateOf(selectedFilters) }
    var unselected by remember { mutableStateOf(unselectedFilters) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .verticalScroll(rememberScrollState())
    ) {
        FiltersBox(
            boxTitle = titleSelected,
            filters = selected,
            isSelected = true,
            onClick = { filter ->
                selected = selected.minus(filter)
                unselected = unselected.plus(filter)

                onValueChange(
                    selected,
                    unselected
                )
            },
            textOtherEmpty = textOtherEmpty,
            textSelectedEmpty = textSelectedEmpty
        )

        FiltersBox(
            boxTitle = titleOther,
            filters = unselected,
            isSelected = false,
            onClick = { filter ->
                unselected = unselected.minus(filter)
                selected = selected.plus(filter)

                onValueChange(
                    selected,
                    unselected
                )
            },
            textOtherEmpty = textOtherEmpty,
            textSelectedEmpty = textSelectedEmpty
        )
    }
}