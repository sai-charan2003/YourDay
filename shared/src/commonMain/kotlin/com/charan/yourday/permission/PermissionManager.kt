package com.charan.yourday.permission

import kotlinx.coroutines.flow.Flow

interface PermissionManager {

    fun isPermissionGranted(permissions: Permissions) : Boolean
    fun requestPermission(permissions: Permissions)
    fun requestMultiplePermissions(permissions: List<Permissions>)
    fun openAppSettings()
    fun observeCalenderPermission() : Flow<Boolean>


}