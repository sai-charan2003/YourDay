package com.charan.yourday.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication


actual class PlatformSettings {
    actual fun openSettings(): Boolean {
        val url = NSURL(string = "app-settings:")
        UIApplication.sharedApplication.openURL(url)
        return true
    }
}