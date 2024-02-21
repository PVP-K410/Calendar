package com.pvp.app.ui.view.component.calendar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pvp.app.ui.theme.CalendarTheme
import java.time.DayOfWeek

@Preview(showSystemUi = true)
@Composable
fun Week() {
    CalendarTheme {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            (1..7).forEach {
                Day(DayOfWeek.of(it).name)
            }
        }
    }
}
