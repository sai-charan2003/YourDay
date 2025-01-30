package com.charan.yourday.permission

import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.Flow

interface PermissionManager {

    fun hasCalenderPermission() : Boolean
    fun requestCalenderPermission()
}