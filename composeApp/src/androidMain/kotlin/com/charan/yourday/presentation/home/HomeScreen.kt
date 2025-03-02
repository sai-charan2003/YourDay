package com.charan.yourday.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.home.HomeEvent
import com.charan.yourday.presentation.home.components.CalendarCard
import com.charan.yourday.presentation.home.components.TodoCard
import com.charan.yourday.presentation.home.components.TopBarTitleContent
import com.charan.yourday.presentation.home.components.WeatherCard
import com.charan.yourday.home.HomeScreenComponent
import com.charan.yourday.home.HomeViewEffect
import com.charan.yourday.utils.ProcessState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    component : HomeScreenComponent,
) {
    val context = LocalContext.current


    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val calenderPermissionState = rememberPermissionState(Manifest.permission.READ_CALENDAR)
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showDropDown by remember { mutableStateOf(false) }
    val homeState by component.state.collectAsState()
    LaunchedEffect(component.effects) {
        component.effects.collectLatest {
            when(it){
                is HomeViewEffect.ShowToast -> {
                    Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                }
                HomeViewEffect.RequestCalenderPermission -> {
                    calenderPermissionState.launchPermissionRequest()
                }
                HomeViewEffect.RequestLocationPermission -> {
                    locationPermissionState.launchPermissionRequest()
                }
            }
        }
    }
    LaunchedEffect(locationPermissionState.status) {
        when (locationPermissionState.status) {
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                Log.d("TAG", "HomeScreen: granted")
                component.onEvent(HomeEvent.FetchWeather)
            }
        }
    }
    LaunchedEffect(calenderPermissionState.status) {
        when (calenderPermissionState.status) {
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                component.onEvent(HomeEvent.FetchCalendarEvents)

            }
        }
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                },
                scrollBehavior = scroll,
                actions = {
                    IconButton(
                        onClick = {
                            showDropDown = true
                        }

                    ) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showDropDown,
                        onDismissRequest = {
                            showDropDown = false
                        }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showDropDown = false
                                component.onEvent(HomeEvent.OpenSettingsPage)
                            },
                            text = {
                                Text("Settings")
                            }
                        )
                    }
                }
            )

        },
        modifier = Modifier
    ) { padding ->

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .nestedScroll(scroll.nestedScrollConnection)
                .fillMaxSize()
                .padding(15.dp)

        ) {
            item {
                TopBarTitleContent(Modifier.padding(bottom = 20.dp))
                WeatherCard(
                    weatherState = homeState.weatherState,
                    onLocationPermissionAccess = {
                        component.onEvent(HomeEvent.RequestLocationPermission(locationPermissionState.status.shouldShowRationale))

                    },
                )
                Spacer(Modifier.padding(vertical = 10.dp))
                CalendarCard(
                    calenderState = homeState.calenderData,
                    modifier = Modifier.padding(top = 20.dp),
                    grantPermission = {
                        component.onEvent(HomeEvent.RequestCalendarPermission(calenderPermissionState.status.shouldShowRationale))

                    },

                )
                Spacer(Modifier.padding(vertical = 10.dp))

                TodoCard(
                    todoState = homeState.todoState,
                    onConnect = {
                        component.onEvent(HomeEvent.ConnectTodoist)
                    },
                    onTodoOpen = { link ->
                        component.onEvent(HomeEvent.OnOpenLink(link))
                    }

                )

            }
        }

    }
}





