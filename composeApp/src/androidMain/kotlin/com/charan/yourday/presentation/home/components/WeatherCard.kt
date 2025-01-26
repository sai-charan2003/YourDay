package com.charan.yourday.presentation.home.components
import android.util.Log
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
import com.charan.yourday.data.remote.responseDTO.WeatherDTO


@Composable
fun WeatherCard(
    weatherDTO: WeatherDTO,
    isLoading: Boolean,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isLoading) "Fetching..." else weatherDTO.getLocation(),
                    style = MaterialTheme.typography.titleMedium
                )
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Sunny",
                        tint = Color(0xFFFFD700)
                    )
                }
            }

            if (isLoading) {

                Text("Loading weather data...", style = MaterialTheme.typography.bodyMedium)
            } else {

                Text("${weatherDTO.getMaxTemperatureInC()}°C", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Min: ${weatherDTO.getMinTemperatureInC()}°C", style = MaterialTheme.typography.bodyMedium)
                    Text("Max: ${weatherDTO.getMaxTemperatureInC()}°C", style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = weatherDTO.getCurrentCondition(),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF00FF00))
                )
            }
        }
    }
}
