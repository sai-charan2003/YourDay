package com.charan.yourday.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PermissionManagerImp(
    private val context : Context,

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

    }

    override fun requestMultiplePermissions(permissions: List<Permissions>) {


    }

    override fun openAppSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override fun observeCalenderPermission(): Flow<Boolean> {
        return MutableStateFlow(false)

    }

    private fun getPlatformPermission(permissions: Permissions) : String {
        return when(permissions) {
            Permissions.CALENDER -> Manifest.permission.READ_CALENDAR
            Permissions.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
        }

    }





}