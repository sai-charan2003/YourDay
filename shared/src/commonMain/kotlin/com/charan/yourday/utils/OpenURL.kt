package com.charan.yourday.utils

import org.koin.core.component.KoinComponent

expect object OpenURL : KoinComponent {

    fun openURL(url : String)
}