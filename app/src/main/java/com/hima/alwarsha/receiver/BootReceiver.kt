package com.hima.alwarsha.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.hima.alwarsha.service.DrivingTrackingService
import com.hima.alwarsha.util.TrackingPreferences

/** Restarts driving tracking after a device reboot if the user had it enabled. */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!TrackingPreferences.isEnabled(context)) return

        val serviceIntent = Intent(context, DrivingTrackingService::class.java)
            .setAction(DrivingTrackingService.ACTION_START_TRACKING)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
