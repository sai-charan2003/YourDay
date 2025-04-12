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
    calenderState : CalenderState,
    grantPermission : () -> Unit) {

    ContentElevatedCard(
        title = "Today's Events",
        isLoading = calenderState.isLoading,
        hasError = calenderState.error.isNullOrEmpty().not(),
        hasContent = calenderState.calenderData != null,
        content = {
            if (!calenderState.isCalenderPermissionGranted) {
                GrantPermissionContent("Please Grant Permission to access calender") {
                    grantPermission()

                }
                return@ContentElevatedCard
            }
            if (calenderState.calenderData.isNullOrEmpty().not()) {
                for (event in calenderState.calenderData!!) {
                    EventItem(event)
                }
                return@ContentElevatedCard

            }
            if (calenderState.calenderData.isNullOrEmpty()) {
                NoEventItem()
                return@ContentElevatedCard
            }

        }
    )


}
