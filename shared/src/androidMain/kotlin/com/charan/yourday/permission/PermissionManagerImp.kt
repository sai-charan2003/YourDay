package com.charan.yourday.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManagerImp(
    private val context : Context
) : PermissionManager {
    companion object {
        private const val PERMISSION_CODE = 1001
    }
    override fun isPermissionGranted(permissions: Permissions): Boolean {
        return ContextCompat.checkSelfPermission(
            context,getPlatformPermission(permissions)
        ) == PackageManager.PERMISSION_GRANTED

    }

    override fun requestPermission(permissions: Permissions)  {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(getPlatformPermission(permissions)),
            PERMISSION_CODE

        )
    }

    override fun requestMultiplePermissions(permissions: List<Permissions>) {
        val permissionList = permissions.map {
            getPlatformPermission(it)
        }.toTypedArray()
        ActivityCompat.requestPermissions(
            context as Activity,
            permissionList,
            PERMISSION_CODE
        )

    }

    override fun openAppSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun getPlatformPermission(permissions: Permissions) : String {
        return when(permissions) {
            Permissions.CALENDER -> Manifest.permission.READ_CALENDAR
            Permissions.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
        }

    }





}