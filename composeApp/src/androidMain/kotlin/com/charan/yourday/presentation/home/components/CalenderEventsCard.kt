package com.charan.yourday.presentation.home.components

import androidx.compose.animation.animateContentSize
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
fun CalendarCard(
    calenderEvents : List<CalenderItems>
    ,modifier: Modifier= Modifier,
    isPermissionGranted : Boolean,
    grantPermission : () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().then(modifier).animateContentSize(),
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
            if(!isPermissionGranted) GrantPermissionContent("Please Grant Permission to access calender") {
                grantPermission()

            } else {
                Text(
                    text = "Today's Events",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                if(calenderEvents.isEmpty()){
                    NoEventItem()
                } else {
                    for (event in calenderEvents) {
                        EventItem(event)
                    }
                }
            }



        }
    }
}

//@Composable
//@Preview()
//fun CalendarCardPreview() {
//    val calenderItem = CalenderItems(
//        title = "Team Meeting",
//        stateTime = 1738144788145,
//        endTime = 1738161000000,
//        calenderColor = "-6299161"
//    )
//
//    MaterialTheme {
//        Surface {
//            CalendarCard(listOf(calenderItem,calenderItem))
//        }
//    }
//}
