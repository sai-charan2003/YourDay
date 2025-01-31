package com.charan.yourday.permission

interface PermissionManager {

    fun isPermissionGranted(permissions: Permissions) : Boolean
    fun requestPermission(permissions: Permissions)
    fun requestMultiplePermissions(permissions: List<Permissions>)
    fun openAppSettings()


}