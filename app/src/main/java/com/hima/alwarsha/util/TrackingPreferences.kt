package com.hima.alwarsha.util

import android.content.Context

/** Single boolean flag for whether automatic GPS driving-tracking is enabled. */
object TrackingPreferences {
    private const val PREFS_NAME = "alwarsha_tracking_prefs"
    private const val KEY_ENABLED = "tracking_enabled"
    private const val KEY_LAST_UPDATE_EPOCH = "tracking_last_update_epoch"

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun setLastUpdateEpoch(context: Context, epochMillis: Long) {
        prefs(context).edit().putLong(KEY_LAST_UPDATE_EPOCH, epochMillis).apply()
    }

    fun getLastUpdateEpoch(context: Context): Long = prefs(context).getLong(KEY_LAST_UPDATE_EPOCH, 0L)

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
