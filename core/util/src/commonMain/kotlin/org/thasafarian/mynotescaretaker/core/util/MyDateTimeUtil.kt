package org.thasafarian.mynotescaretaker.core.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object MyDateTimeUtil {

    @OptIn(ExperimentalTime::class)
    fun currentDate(): String {
        val now = Clock.System.now()
        return now.toString()
    }
}

