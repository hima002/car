package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.service.DrivingTrackingService
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

/**
 * Receives IN_VEHICLE enter/exit transitions from ActivityRecognitionClient and
 * relays them to [DrivingTrackingService] to start/stop GPS location updates.
 * Registered as a manifest receiver (not dynamic) so transitions are still
 * delivered reliably even if the app process was killed.
 */
class ActivityTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityTransitionResult.hasResult(intent)) return
        val result = ActivityTransitionResult.extractResult(intent) ?: return

        for (event in result.transitionEvents) {
            if (event.activityType != DetectedActivity.IN_VEHICLE) continue

            val action = when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> DrivingTrackingService.ACTION_START_LOCATION_UPDATES
                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> DrivingTrackingService.ACTION_STOP_LOCATION_UPDATES
                else -> null
            } ?: continue

            val serviceIntent = Intent(context, DrivingTrackingService::class.java).setAction(action)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
