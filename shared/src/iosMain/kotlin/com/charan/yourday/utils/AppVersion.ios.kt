package com.charan.yourday.utils

import platform.Foundation.NSBundle

actual fun appVersion() : String{

    return NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion").toString()
}