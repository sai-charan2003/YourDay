package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.data.model.WeatherData
import dev.icerock.moko.resources.compose.painterResource

@Composable

fun WeatherDataItem(weatherData: WeatherData){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = weatherData?.location ?: "",
            style = MaterialTheme.typography.titleMedium
        )

        Image(
            painter = painterResource(
                weatherData?.temperatureIcon ?: MR.images.icy
            ),
            null,
            modifier = Modifier.size(24.dp)
        )

    }


    Text(
        weatherData?.currentTemperature ?: "",
        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
    )

}