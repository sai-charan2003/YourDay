package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.yourday.data.model.CalenderItems

@Composable
fun CalendarCard(calenderEvents : List<CalenderItems>,modifier: Modifier= Modifier) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().then(modifier),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 15.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 15.dp
                )
                .fillMaxWidth()
        ) {
            Text(
                text = "Today's Events",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            for(event in calenderEvents){
                EventItem(event)
            }



        }
    }
}

@Composable
@Preview()
fun CalendarCardPreview() {
    val calenderItem = CalenderItems(
        title = "Team Meeting",
        stateTime = 1738144788145,
        endTime = 1738161000000,
        calenderColor = "-6299161"
    )

    MaterialTheme {
        Surface {
            CalendarCard(listOf(calenderItem,calenderItem))
        }
    }
}
