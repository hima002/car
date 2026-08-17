package com.hima.alwarsha.util

import java.util.Calendar

/** Truncates an epoch-millis timestamp to the start of its local calendar day. */
object DayEpoch {
    fun startOfDay(epochMillis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = epochMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun daysAgo(days: Int): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }
        return startOfDay(calendar.timeInMillis)
    }
}
