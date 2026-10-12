package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.cloudy
import com.charan.yourday.presentation.home.ForecastWeatherState
import com.charan.yourday.presentation.home.WeatherState
import com.charan.yourday.ui.theme.IndexItem
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun WeatherCard(
    weatherState: WeatherState,
    grantPermission: () -> Unit,
    scrollToCurrentTimeIndex: Int,
    modifier: Modifier = Modifier
) {
    WeatherCard(
        isLoading = weatherState.isLoading,
        error = weatherState.error,
        hasContent = weatherState.currentWeather != null,
        location = weatherState.currentWeather?.location,
        currentWeatherIcon = weatherState.currentWeather?.icon,
        currentTemperature = weatherState.currentWeather?.temp?.toString(),
        isPermissionGranted = weatherState.isLocationPermissionGranted,
        weatherConditionText = weatherState.currentWeather?.condition.orEmpty(),
        onLocationPermissionAccess = grantPermission,
        weatherUnits = weatherState.weatherUnits,
        forecastData = weatherState.forecastWeather,
        scrollToCurrentTimeIndex = scrollToCurrentTimeIndex,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherCard(
    isLoading: Boolean,
    error: String?,
    hasContent: Boolean,
    location: String?,
    currentWeatherIcon: ImageResource?,
    currentTemperature: String?,
    isPermissionGranted: Boolean,
    weatherConditionText: String,
    onLocationPermissionAccess: () -> Unit,
    weatherUnits: String,
    forecastData: List<ForecastWeatherState>,
    scrollToCurrentTimeIndex: Int,
    modifier: Modifier = Modifier
) {
    val lazyScrollState = rememberLazyListState()

    LaunchedEffect(scrollToCurrentTimeIndex) {
        if (scrollToCurrentTimeIndex != 0) {
            lazyScrollState.animateScrollToItem(scrollToCurrentTimeIndex)
        }
    }

    ContentElevatedCard(
        isLoading = isLoading,
        hasError = error,
        hasContent = hasContent,
        modifier = modifier,
        content = {
            if (!isPermissionGranted) {
                GrantPermissionContent(
                    "Please Grant Permission to access weather information"
                ) {
                    onLocationPermissionAccess()
                }
            } else {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = location.orEmpty(),
                                    style = MaterialTheme.typography.labelMediumEmphasized,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${currentTemperature.orEmpty()}°$weatherUnits",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                            )

                            Text(
                                text = weatherConditionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Image(
                            painter = painterResource(
                                currentWeatherIcon ?: MR.images.cloudy
                            ),
                            contentDescription = weatherConditionText,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    if (forecastData.isNotEmpty()) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )

                        LazyRow(
                            state = lazyScrollState,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            itemsIndexed(forecastData) { index, item ->
                                WeatherForecastItem(
                                    item = item,
                                    weatherUnits = weatherUnits,
                                    indexItem = IndexItem.FIRST_AND_LAST,
                                    isCurrentTimeItem = index == scrollToCurrentTimeIndex
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
