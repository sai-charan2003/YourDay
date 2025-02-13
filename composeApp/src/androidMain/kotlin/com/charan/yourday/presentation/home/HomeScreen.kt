package com.charan.yourday.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.presentation.home.components.CalendarCard
import com.charan.yourday.presentation.home.components.TodoCard
import com.charan.yourday.presentation.home.components.TopBarTitleContent
import com.charan.yourday.presentation.home.components.WeatherCard
import com.charan.yourday.home.HomeScreenComponent
import com.charan.yourday.utils.ProcessState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    component : HomeScreenComponent,
) {
    val context = LocalContext.current
    val weatherStatus = component.weatherData.collectAsState(ProcessState.Loading)
    val calenderEvents = component.calenderEvents.collectAsState(emptyList())
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val calenderPermissionState = rememberPermissionState(Manifest.permission.READ_CALENDAR)
    val todoistAuthorizationFlow by component.todoistAuthorizationFlow.collectAsState(ProcessState.NotDetermined)
    val tokenFlow by component.todoistAuthToken.collectAsState(null)
    val todoTasks by component.todoistTasks.collectAsState(ProcessState.NotDetermined)
    val errorCode by component.isErrorSent.collectAsState(false)
    LaunchedEffect(component.isErrorSent) {
        if(errorCode == true){
            Toast.makeText(context,"Unable to authenticate",Toast.LENGTH_LONG).show()

        }

    }
    LaunchedEffect(todoTasks) {
        when(todoTasks){
            is ProcessState.Error -> {
                component.clearTodoistToken()
                Toast.makeText(context,"Please connect again",Toast.LENGTH_LONG).show()
            }
            else -> Unit

        }
    }
    LaunchedEffect(locationPermissionState.status) {
        when(locationPermissionState.status){
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                component.getLocation()
            }
        }
    }
    LaunchedEffect(calenderPermissionState.status) {
        when(calenderPermissionState.status){
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                component.getCalenderEvents()
            }
        }
    }




    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    TopBarTitleContent()
                }
            )
        },
    ) { padding ->

        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(15.dp)) {
            item {
                WeatherCard(
                    weatherDTO = weatherStatus.value.extractData() ?: WeatherDTO(),
                    isLoading = weatherStatus.value.isLoading(),
                    isLocationPermissionGranted = locationPermissionState.status.isGranted,
                    onLocationPermissionAccess = {
                        component.grantLocationPermission(locationPermissionState.status.shouldShowRationale)


                    },
                )
                CalendarCard(
                    calenderEvents = calenderEvents.value,
                    modifier = Modifier.padding(top = 20.dp),
                    grantPermission = {
                        component.grantCalenderPermission(calenderPermissionState.status.shouldShowRationale)

                    },
                    isPermissionGranted = calenderPermissionState.status.isGranted
                )

                TodoCard(
                    isAuthenticating = todoistAuthorizationFlow.isLoading(),
                    onClick = {
                        component.requestTodoistAuthentication()
                    },
                    todoContent = todoTasks.extractData() ?: emptyList(),
                    isContentLoading = todoTasks.isLoading(),
                    showContent = tokenFlow.isNullOrEmpty().not()
                )

            }
        }

    }


}

private fun getPermission(context : Context,requestPermission : () -> Unit, openSettings: () -> Unit) {
    if(ActivityCompat
        .shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION)){
        openSettings()


    } else{
        requestPermission()
    }
}
