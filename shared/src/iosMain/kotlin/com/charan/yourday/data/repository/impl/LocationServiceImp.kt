package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.local.Location
import com.charan.yourday.data.repository.LocationServiceRepo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import kotlin.coroutines.suspendCoroutine

class LocationServiceImp : LocationServiceRepo {
    private val locationManager = CLLocationManager()

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocation(): Location? {
        return locationManager.location?.coordinate?.useContents {
            Location(latitude, longitude)
        }
    }

}