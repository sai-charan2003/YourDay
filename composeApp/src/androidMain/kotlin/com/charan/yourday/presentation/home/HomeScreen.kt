package com.charan.yourday.presentation.home

import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.presentation.home.components.CalendarCard
import com.charan.yourday.presentation.home.components.TopBarTitleContent
import com.charan.yourday.presentation.home.components.WeatherCard
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import com.charan.yourday.viewmodels.HomeScreenViewModel
import dev.icerock.moko.permissions.PermissionState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.get
import org.koin.dsl.module
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val weatherStatus = viewModel.weatherData.collectAsState(ProcessState.Loading)
    val calenderEvents = viewModel.calenderEvents.collectAsState(emptyList())



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
                CalendarCard(
                    calenderEvents = calenderEvents.value,
                    modifier = Modifier.padding(top = 20.dp)

                )

            }
        }

    }

}
