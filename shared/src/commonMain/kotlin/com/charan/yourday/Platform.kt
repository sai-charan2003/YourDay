package com.charan.yourday

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform