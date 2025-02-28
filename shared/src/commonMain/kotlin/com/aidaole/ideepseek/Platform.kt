package com.aidaole.ideepseek

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform