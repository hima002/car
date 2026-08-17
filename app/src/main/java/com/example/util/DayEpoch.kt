package com.example.util

import java.util.Calendar

/**
 * Truncates an epoch-millis timestamp to the start of its local calendar day.
 * Used as the grouping key for daily trip-distance rows so repeated GPS
 * updates on the same day accumulate into a single row instead of duplicating.
 */
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
