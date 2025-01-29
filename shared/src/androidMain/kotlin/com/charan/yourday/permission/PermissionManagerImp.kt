package com.charan.yourday.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManagerImp(
    private val context : Context
) : PermissionManager {
    companion object {
        private const val CALENDAR_PERMISSION_REQUEST_CODE = 1001
    }
    override fun hasCalenderPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED

    }

    override fun requestCalenderPermission() {
        if (!hasCalenderPermission()) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                ),
                CALENDAR_PERMISSION_REQUEST_CODE
            )
        }

    }

}