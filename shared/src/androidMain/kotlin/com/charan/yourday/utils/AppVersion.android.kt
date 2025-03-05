package com.charan.yourday.utils

import android.content.Context
import android.os.Build
import com.charan.yourday.shared.BuildConfig
import com.charan.yourday.utils.OpenURL.getKoin

actual fun appVersion(): String {
    val context = getKoin().get<Context>()
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }

}