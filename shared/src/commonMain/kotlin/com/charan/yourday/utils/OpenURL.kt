package com.charan.yourday.utils

import androidx.compose.ui.platform.LocalUriHandler
import org.koin.core.component.KoinComponent

expect object openURL : KoinComponent {

    fun openURL(url : String)
}