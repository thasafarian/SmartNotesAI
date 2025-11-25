package org.thasafarian.mynotescaretaker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform