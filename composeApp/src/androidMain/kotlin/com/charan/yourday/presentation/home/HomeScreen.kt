package com.charan.yourday.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavHostController
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.presentation.home.components.CalendarCard
import com.charan.yourday.presentation.home.components.TodoCard
import com.charan.yourday.presentation.home.components.TopBarTitleContent
import com.charan.yourday.presentation.home.components.WeatherCard
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import com.charan.yourday.viewmodels.HomeScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.get
import org.koin.dsl.module
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    authorizationId : String? = null
) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val context = LocalContext.current
    val weatherStatus = viewModel.weatherData.collectAsState(ProcessState.Loading)
    val calenderEvents = viewModel.calenderEvents.collectAsState(emptyList())
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val calenderPermissionState = rememberPermissionState(Manifest.permission.READ_CALENDAR)
    val todoistAuthorizationFlow by viewModel.todoistAuthorizationFlow.collectAsState(ProcessState.NotDetermined)
    val tokenFlow by viewModel.todoistAuthToken.collectAsState(null)
    val todoTasks by viewModel.todoistTasks.collectAsState(ProcessState.NotDetermined)
    LaunchedEffect(authorizationId) {
        if(authorizationId!=null){
            viewModel.getTodoistAccessToken(authorizationId)
        }
    }
    LaunchedEffect(tokenFlow) {
        if(tokenFlow!=null){
            Log.d("TAG", "HomeScreen: $tokenFlow")
            viewModel.getTodoistTodayTasks(tokenFlow!!)
        }
    }
    LaunchedEffect(todoistAuthorizationFlow) {
        when(todoistAuthorizationFlow){
            is ProcessState.Error -> {
                Toast.makeText(context, (todoistAuthorizationFlow as ProcessState.Error).message,Toast.LENGTH_LONG).show()

            }
            ProcessState.Loading -> {

            }
            ProcessState.NotDetermined -> {

            }
            is ProcessState.Success -> {
                if((todoistAuthorizationFlow as ProcessState.Success<TodoistTokenDTO>).data.access_token!=null) {
                    viewModel.saveTodoistToken((todoistAuthorizationFlow as ProcessState.Success<TodoistTokenDTO>).data.access_token!!)
                    viewModel.getToken()
                }
            }
        }
    }
    LaunchedEffect(locationPermissionState.status) {
        when(locationPermissionState.status){
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                viewModel.getLocation()
            }
        }
    }
    LaunchedEffect(calenderPermissionState.status) {
        when(calenderPermissionState.status){
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                viewModel.getCalenderEvents()
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
        }
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
                        viewModel.grantLocationPermission(locationPermissionState.status.shouldShowRationale)


                    },
                )
                CalendarCard(
                    calenderEvents = calenderEvents.value,
                    modifier = Modifier.padding(top = 20.dp),
                    grantPermission = {
                        viewModel.grantCalenderPermission(calenderPermissionState.status.shouldShowRationale)

                    },
                    isPermissionGranted = calenderPermissionState.status.isGranted
                )

                TodoCard(
                    isAuthenticating = todoistAuthorizationFlow.isLoading(),
                    onClick = {
                        viewModel.requestTodoistAuthentication()
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
