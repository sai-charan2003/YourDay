package com.charan.yourday.presentation.onboarding

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.launch
import com.charan.yourday.MR
import com.charan.yourday.home.HomeEvent
import com.charan.yourday.home.HomeScreenComponent
import com.charan.yourday.home.HomeState
import com.charan.yourday.home.HomeViewEffect
import com.charan.yourday.onBoarding.OnBoardingEvent
import com.charan.yourday.onBoarding.OnBoardingScreenComponent
import com.example.compose.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnBoardingScreen(
    component: HomeScreenComponent,
) {

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val calendarPermissionState = rememberPermissionState(Manifest.permission.READ_CALENDAR)
    val state by component.state.collectAsState()

    LaunchedEffect(locationPermissionState.status) {
        when (locationPermissionState.status) {
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                component.onEvent(HomeEvent.FetchWeather)
            }
        }
    }

    LaunchedEffect(calendarPermissionState.status) {
        when (calendarPermissionState.status) {
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                component.onEvent(HomeEvent.FetchCalendarEvents)
            }
        }
    }

    LaunchedEffect(component.effects) {
        component.effects.collectLatest { effect ->
            when (effect) {
                HomeViewEffect.RequestCalenderPermission -> {
                    calendarPermissionState.launchPermissionRequest()
                }
                HomeViewEffect.RequestLocationPermission -> {
                    locationPermissionState.launchPermissionRequest()
                }
                else -> Unit
            }
        }
    }
    LaunchedEffect(component.authorizationId) {
        if(component.authorizationId !=null){
            pagerState.scrollToPage(1)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .then(
                    if(pagerState.currentPage == 0) {
                        Modifier
                    } else {
                        Modifier.padding(padding).fillMaxSize()
                    }

                )
                ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { currentPage ->
                    when (currentPage) {
                        0 -> WelcomeScreen(
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                        )
                        1 -> PermissionsScreen(
                            onCalendarAccess = {
                                component.onEvent(HomeEvent.RequestCalendarPermission(
                                    calendarPermissionState.status.shouldShowRationale))
                            },
                            onLocationAccess = {
                                component.onEvent(HomeEvent.RequestLocationPermission(
                                    locationPermissionState.status.shouldShowRationale))
                            },
                            homeState = state,
                            onTodoistConnect = {
                                component.onEvent(HomeEvent.ConnectTodoist)
                            },
                            locationPermissionStatus = locationPermissionState.status,
                            calendarPermissionStatus = calendarPermissionState.status,
                            onNextClick = {
                                component.onEvent(HomeEvent.OnBoardingFinish)
                            }
                        )
                    }
                }
            }
        }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeScreen(onClick: () -> Unit) {
    Box(modifier = Modifier

        .fillMaxSize()
        .background(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFE08A7D), Color(0xFF72201D))
            )
        ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    initialScale = 0.5f,
                ) + fadeIn()
            ) {
                OnBoardImage()
            }

            Spacer(modifier = Modifier.height(40.dp))


            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 500, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = 300))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome to Your Day",
                        style = MaterialTheme.typography.headlineMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Your all-in-one companion for planning your day with weather updates, calendar events, and to-do lists.",
                        style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(durationMillis = 500, delayMillis = 600)
            ) + fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = 600)),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FilledTonalButton(
                onClick = {
                    onClick()
                },
                modifier = Modifier
                    .padding(20.dp)
                    ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFDAC6),
                    contentColor = Color(0xFF72201D),
                    disabledContainerColor = Color(0xFFE0B89F)
                ),
                shapes = ButtonDefaults.shapes()
            ) {
                Text("Let's go")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionsScreen(
    onLocationAccess: () -> Unit,
    onCalendarAccess: () -> Unit,
    onTodoistConnect: () -> Unit,
    homeState: HomeState,
    locationPermissionStatus: PermissionStatus,
    calendarPermissionStatus: PermissionStatus,
    onNextClick : () -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn()
            ) {
                Text(
                    text = "Get the Most Out of Your Day!",
                    style = MaterialTheme.typography.headlineMediumEmphasized.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            val animationDelay = 150

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 500, delayMillis = animationDelay)
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = animationDelay
                    )
                )
            ) {
                WeatherFeatureSection(
                    onLocationAccess = onLocationAccess,
                    permissionStatus = locationPermissionStatus,
                    isPermissionGranted = homeState.weatherState.isLocationPermissionGranted
                )
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 500, delayMillis = 2 * animationDelay)
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 2 * animationDelay
                    )
                )
            ) {
                CalendarFeatureSection(
                    onCalendarAccess = onCalendarAccess,
                    permissionStatus = calendarPermissionStatus,
                    isPermissionGranted = homeState.calenderData.isCalenderPermissionGranted
                )
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 500, delayMillis = 3 * animationDelay)
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 3 * animationDelay
                    )
                )
            ) {
                TodoFeatureSection(
                    onTodoistConnect = onTodoistConnect,
                    isTodoConnected = homeState.todoState.isTodoAuthenticated
                )
            }

            Spacer(Modifier.height(24.dp))
        }
        FilledTonalButton(
            onClick = {
                onNextClick()
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
            shapes = ButtonDefaults.shapes()
        ) {
            Text("Next")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherFeatureSection(
    onLocationAccess: () -> Unit,
    permissionStatus: PermissionStatus,
    isPermissionGranted: Boolean
) {
    FeatureSection(
        icon = Icons.Filled.WbSunny,
        title = "Stay Ahead of the Weather",
        description = "Plan your day with real-time weather updates, so you're always prepared, rain or shine!"
    ) {
        when {
            isPermissionGranted || permissionStatus is PermissionStatus.Granted -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Location access granted",
                        style = MaterialTheme.typography.bodyMediumEmphasized
                    )
                }
            }
            else -> {
                OutlinedButton(
                    onClick = onLocationAccess,
                    modifier = Modifier.animateContentSize(),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Enable Location Access")
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarFeatureSection(
    onCalendarAccess: () -> Unit,
    permissionStatus: PermissionStatus,
    isPermissionGranted: Boolean
) {
    FeatureSection(
        icon = Icons.Filled.CalendarMonth,
        title = "Never Miss an Event",
        description = "Sync your calendar and keep track of all your important meetings, birthdays, and appointments."
    ) {
        when {
            isPermissionGranted || permissionStatus is PermissionStatus.Granted -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Calendar access granted",
                        style = MaterialTheme.typography.bodyMediumEmphasized
                    )
                }
            }
            else -> {
                OutlinedButton(
                    onClick = onCalendarAccess,
                    modifier = Modifier.animateContentSize(),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Grant Calendar Access")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodoFeatureSection(
    onTodoistConnect: () -> Unit,
    isTodoConnected: Boolean
) {
    FeatureSection(
        icon = Icons.Filled.DoneAll,
        title = "Manage Your Tasks Effortlessly",
        description = "Integrate with your favorite to-do apps and see all your daily tasks in one place."
    ) {
        if (isTodoConnected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(MR.images.Todoist),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Connected to Todoist",
                    style = MaterialTheme.typography.bodyMediumEmphasized
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(MR.images.Todoist),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("Connect to Todoist")
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onTodoistConnect,
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Connect")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeatureSection(
    icon: ImageVector,
    title: String,
    description: String,
    action: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconHeading(icon)
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMediumEmphasized.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMediumEmphasized
        )
        Spacer(Modifier.height(8.dp))
        action()
        Spacer(Modifier.height(16.dp))
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )
    }
}

@Composable
fun OnBoardImage() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(MR.images.AppIcon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(120.dp)
        )
    }
}

@Composable
fun IconHeading(icon: ImageVector) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}