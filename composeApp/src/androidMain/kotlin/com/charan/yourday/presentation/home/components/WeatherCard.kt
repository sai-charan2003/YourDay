package com.charan.yourday.presentation.home.components
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.home.WeatherState
import dev.icerock.moko.resources.compose.painterResource



@Composable
fun WeatherCard(
    weatherState: WeatherState?,
    onLocationPermissionAccess : () -> Unit
) {

    ContentElevatedCard(
        title = "Today's Weather",
        isLoading = weatherState?.isLoading == true,
        hasError = weatherState?.error,
        hasContent = weatherState?.weatherData != null,
        content = {

            if (weatherState?.isLocationPermissionGranted == false) {
                GrantPermissionContent("Please Enable location permission to fetch weather data") {
                    onLocationPermissionAccess()
                }
                return@ContentElevatedCard
            }
            if (weatherState?.weatherData != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weatherState?.weatherData?.location ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Image(
                        painter = painterResource(
                            weatherState?.weatherData?.temperatureIcon ?: MR.images.icy
                        ),
                        null,
                        modifier = Modifier.size(24.dp)
                    )

                }


                Text(
                    weatherState?.weatherData?.currentTemperature ?: "",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Min: ${weatherState?.weatherData?.minTemperature}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Max: ${weatherState?.weatherData?.maxTemperature}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = weatherState?.weatherData?.currentCondition ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF00FF00))
                )
                return@ContentElevatedCard
            }

        }
    )
}

