package com.charan.yourday.data.repository.impl

import android.annotation.SuppressLint
import android.content.Context
import com.charan.yourday.data.local.Location


import com.charan.yourday.data.repository.LocationServiceRepo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationServiceImp(private val context : Context): LocationServiceRepo{
    private val fusedLocationClient : FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCoroutine{ continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    continuation.resume(Location(it.latitude,it.longitude))
                } ?: run {
                    continuation.resumeWithException(Exception("Unable to get current location"))
                }
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }


    }





}