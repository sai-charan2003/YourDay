package com.charan.yourday.data.repository

import com.charan.yourday.data.model.Location

interface LocationServiceRepo {

    suspend fun getCurrentLocation() : Location?
}