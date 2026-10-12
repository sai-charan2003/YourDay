package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.cloudy
import com.charan.yourday.presentation.common.CustomListItem
import com.charan.yourday.presentation.home.ForecastWeatherState
import com.charan.yourday.ui.theme.IndexItem
import dev.icerock.moko.resources.compose.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherForecastItem(
    modifier: Modifier = Modifier,
    item: ForecastWeatherState,
    weatherUnits: String,
    indexItem: IndexItem = IndexItem.FIRST_AND_LAST,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    isCurrentTimeItem: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    CustomListItem(
        modifier = modifier,
        indexItem = indexItem,
        isHorizontal = true,
        isSelected = isCurrentTimeItem,
        contentPadding = contentPadding,
        onClick = onClick,
        headLineContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.time.orEmpty(),
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    color = if (isCurrentTimeItem) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Image(
                    painter = painterResource(item.icon ?: MR.images.cloudy),
                    contentDescription = item.condition,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = "${item.temp}°$weatherUnits",
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrentTimeItem) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}
