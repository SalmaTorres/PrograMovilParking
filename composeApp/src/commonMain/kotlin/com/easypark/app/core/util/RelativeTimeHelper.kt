package com.easypark.app.core.util

import kotlinx.datetime.Clock

object RelativeTimeHelper {
    fun format(epochMillis: Long): String {
        if (epochMillis <= 0L) return ""
        val now = Clock.System.now().toEpochMilliseconds()
        val diffMillis = now - epochMillis
        val diffSeconds = diffMillis / 1000
        val diffMinutes = diffSeconds / 60
        val diffHours = diffMinutes / 60
        val diffDays = diffHours / 24

        return when {
            diffMillis < 0 -> "Pronto"
            diffSeconds < 60 -> "Hace unos segundos"
            diffMinutes < 60 -> "Hace $diffMinutes min"
            diffHours < 24 -> "Hace $diffHours h"
            else -> "Hace $diffDays días"
        }
    }
}
