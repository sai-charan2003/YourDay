package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.presentation.common.CustomListItem
import com.charan.yourday.presentation.home.CalendarState
import com.charan.yourday.ui.theme.IndexItem
import com.charan.yourday.ui.theme.indexItemFor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarCard(
    calendarState: CalendarState,
    grantPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HomeSectionHeader(
            title = "Today's Events",
            isLoading = calendarState.isLoading,
            hasError = calendarState.error
        )

        if (!calendarState.isCalendarPermissionGranted) {
            CustomListItem(
                indexItem = IndexItem.FIRST_AND_LAST,
                headLineContent = {
                    Text(
                        text = "Calendar Permission",
                        style = MaterialTheme.typography.bodyLargeEmphasized
                    )
                },
                supportingContent = {
                    Text(
                        text = "Please grant permission to access calendar events",
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    FilledTonalButton(
                        onClick = grantPermission,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp),
                        shape = ButtonDefaults.filledTonalShape
                    ) {
                        Text("Grant", style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
            return
        }

        val events = calendarState.calendarData
        if (!events.isNullOrEmpty()) {
            Column {
                events.forEachIndexed { index, event ->
                    EventItem(
                        event = event,
                        indexItem = events.indexItemFor(index)
                    )
                }
            }
        } else {
            NoEventItem()
        }
    }
}
