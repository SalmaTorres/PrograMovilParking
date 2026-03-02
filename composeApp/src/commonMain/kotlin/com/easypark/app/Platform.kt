package com.easypark.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform