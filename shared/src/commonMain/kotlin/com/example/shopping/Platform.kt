package com.example.shopping

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform