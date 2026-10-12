package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.charan.yourday.data.model.CalendarItems
import com.charan.yourday.presentation.common.CustomListItem
import com.charan.yourday.ui.theme.IndexItem
import com.charan.yourday.utils.DateUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EventItem(
    event: CalendarItems,
    indexItem: IndexItem = IndexItem.FIRST_AND_LAST,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null
) {
    val eventColor = Color(color = event.calendarColor?.toLong() ?: 0)
    val timeText = buildString {
        event.stateTime?.let { append(DateUtils.getTimeFromTimeMillis(it)) }
        if (event.stateTime != null && event.endTime != null) append(" - ")
        event.endTime?.let { append(DateUtils.getTimeFromTimeMillis(it)) }
    }

    CustomListItem(
        modifier = modifier,
        indexItem = indexItem,
        contentPadding = contentPadding,
        onClick = onClick,
        leadingContent = {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .clip(CircleShape)
                    .background(eventColor)
            )
        },
        headLineContent = {
            Text(
                text = event.title.orEmpty(),
                style = MaterialTheme.typography.bodyLargeEmphasized,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = if (timeText.isNotEmpty()) {
            {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else null
    )
}
