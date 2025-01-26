package com.charan.yourday.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.data.remote.responseDTO.WeatherDTO
import com.charan.yourday.presentation.home.components.TopBarTitleContent
import com.charan.yourday.presentation.home.components.WeatherCard
import com.charan.yourday.viewmodels.HomeScreenViewModel
import dev.icerock.moko.permissions.PermissionState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val weatherStatus = viewModel.weatherData.collectAsState()


    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    TopBarTitleContent()
                }
            )
        }
    ) { padding ->

        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(15.dp)) {
            item {
                WeatherCard(
                    weatherDTO = weatherStatus.value.extractData() ?: WeatherDTO(),
                    isLoading = weatherStatus.value.isLoading(),
                )

            }
        }

    }

}