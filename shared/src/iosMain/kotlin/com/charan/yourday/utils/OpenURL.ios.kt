package com.charan.yourday.utils

import org.koin.core.component.KoinComponent
import platform.Foundation.NSURL
import platform.UIKit.UIApplication


actual object OpenURL : KoinComponent {

    actual fun openURL(url: String) {
        val nsURL = NSURL(string = url)
        UIApplication.sharedApplication.openURL(nsURL, mapOf<Any?, Any>(), null)
    }
}