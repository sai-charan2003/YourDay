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
import com.charan.yourday.home.CalenderState

@Composable
fun CalendarCard(
    calenderState : CalenderState
    ,modifier: Modifier= Modifier,
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
            if (!calenderState.isCalenderPermissionGranted) {
                GrantPermissionContent("Please Grant Permission to access calender") {
                    grantPermission()

                }
                return@ElevatedCard
            }
            if (calenderState.calenderData.isNullOrEmpty().not()) {
                Text(
                    text = "Today's Events",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                for (event in calenderState.calenderData!!) {
                    EventItem(event)
                }
                return@ElevatedCard

            }
            if(calenderState.error !=null){
                ErrorCard()
                return@ElevatedCard
            }
            if(calenderState.calenderData.isNullOrEmpty()){
                NoEventItem()
                return@ElevatedCard
            }

        }


    }
}
