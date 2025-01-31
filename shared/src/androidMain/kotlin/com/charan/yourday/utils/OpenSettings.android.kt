package com.charan.yourday.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import org.koin.compose.getKoin
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.KoinContext


actual class PlatformSettings (private val context: Context){
    actual fun openSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}