package com.charan.yourday.data.repository

import com.charan.yourday.data.local.Location

interface LocationServiceRepo {

    suspend fun getCurrentLocation() : Location?
}