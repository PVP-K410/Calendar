package com.pvp.app.ui.screen.feedback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.R
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.EditableTextItem
import com.pvp.app.ui.common.TabSelector

@Composable
fun FeedbackCreationDialog(
    isOpen: Boolean,
    onClose: () -> Unit
) {
    if (!isOpen) {
        return
    }

    Dialog(onDismissRequest = onClose) {
        FeedbackForm(onCreate = onClose)
    }
}

@Composable
fun FeedbackForm(
    model: FeedbackViewModel = hiltViewModel(),
    onCreate: () -> Unit
) {
    var bug by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    val isFormValid by remember { derivedStateOf { description.isNotEmpty() } }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabSelector(
                onSelect = { bug = it == 0 },
                tab = if (bug) 0 else 1,
                tabs = listOf(
                    stringResource(R.string.feedback_form_bug),
                    stringResource(R.string.feedback_form_feature_request)
                ),
                withShadow = false
            )

            Spacer(modifier = Modifier.padding(6.dp))

            EditableTextItem(
                editLabel = stringResource(R.string.input_field_description_edit_label),
                errorMessage = stringResource(R.string.input_field_description_error_cannot_be_empty),
                label = stringResource(R.string.input_field_description_label),
                value = description,
                validate = { it.isNotBlank() },
                onValueChange = { description = it },
            )

            Spacer(modifier = Modifier.padding(6.dp))

            Text(
                text = stringResource(R.string.feedback_form_rating),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.padding(6.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Star icon",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { rating = i },
                        tint = when (i <= rating) {
                            true -> MaterialTheme.colorScheme.primary
                            false -> MaterialTheme.colorScheme.surfaceDim
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(6.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = when (isFormValid) {
                        true -> MaterialTheme.colorScheme.surface
                        else -> Color.Gray
                    }
                ),
                onClick = {
                    model.create(
                        bug = bug,
                        description = description,
                        rating = rating
                    )

                    onCreate()
                },
                enabled = isFormValid,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(stringResource(R.string.action_create))
            }
        }
    }
}
