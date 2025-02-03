package com.charan.yourday.utils

import org.koin.core.component.KoinComponent

expect object openURL : KoinComponent {
    fun openURL(url : String)
}