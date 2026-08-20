package com.hima.alwarsha.util

import android.content.Context

/** Single boolean flag for whether automatic GPS driving-tracking is enabled. */
object TrackingPreferences {
    private const val PREFS_NAME = "alwarsha_tracking_prefs"
    private const val KEY_ENABLED = "tracking_enabled"
    private const val KEY_LAST_UPDATE_EPOCH = "tracking_last_update_epoch"
    private const val KEY_ODOMETER_CARRY_KM = "tracking_odometer_carry_km"

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    /**
     * Fractional km left over after the last flush's whole-km part was applied to the odometer,
     * so consecutive sub-1km flushes still add up instead of each being truncated to zero.
     */
    fun getOdometerCarryKm(context: Context): Float = prefs(context).getFloat(KEY_ODOMETER_CARRY_KM, 0f)

    fun setOdometerCarryKm(context: Context, carryKm: Float) {
        prefs(context).edit().putFloat(KEY_ODOMETER_CARRY_KM, carryKm).apply()
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun setLastUpdateEpoch(context: Context, epochMillis: Long) {
        prefs(context).edit().putLong(KEY_LAST_UPDATE_EPOCH, epochMillis).apply()
    }

    fun getLastUpdateEpoch(context: Context): Long = prefs(context).getLong(KEY_LAST_UPDATE_EPOCH, 0L)

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
