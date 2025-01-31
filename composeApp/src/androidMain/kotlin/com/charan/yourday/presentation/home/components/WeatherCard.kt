package com.charan.yourday.presentation.home.components
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import dev.icerock.moko.resources.compose.painterResource
import java.security.Permission


@Composable
fun WeatherCard(
    weatherDTO: WeatherDTO,
    isLoading: Boolean,
    isLocationPermissionGranted: Boolean,
    onLocationPermissionAccess : () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(!isLocationPermissionGranted) GrantPermissionContent("Please Enable location permission to fetch weather data") {
                onLocationPermissionAccess()

            }
            else if (isLoading) FetchingDataContent()
            else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weatherDTO.getLocation(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Image(
                        painter = painterResource(weatherDTO.getImageIcon() ?: MR.images.icy),
                        null,
                        modifier = Modifier.size(24.dp)
                    )

                }


                Text(
                    "${weatherDTO.getCurrentTemperatureInC()}°C",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Min: ${weatherDTO.getMinTemperatureInC()}°C",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Max: ${weatherDTO.getMaxTemperatureInC()}°C",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = weatherDTO.getCurrentCondition(),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF00FF00))
                )
            }
        }

    }
}
