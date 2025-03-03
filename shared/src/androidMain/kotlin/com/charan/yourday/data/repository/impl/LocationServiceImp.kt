package com.charan.yourday.data.repository.impl

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.charan.yourday.data.model.Location


import com.charan.yourday.data.repository.LocationServiceRepo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationServiceImp(private val context : Context): LocationServiceRepo{
    private val fusedLocationClient : FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCoroutine{ continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                Log.d("TAG", "getCurrentLocation: $location")
                location?.let {
                    continuation.resume(Location(it.latitude,it.longitude))
                }
            }.addOnFailureListener { e ->
                Log.d("TAG", "getCurrentLocation: ${e.message}")
                continuation.resumeWithException(e)
            }


    }





}