package com.example.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.database.AppDatabase
import com.example.data.repository.CarRepository
import com.example.receiver.ActivityTransitionReceiver
import com.example.util.NotificationHelper
import com.example.util.TrackingPreferences
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Foreground service that measures real driving distance automatically.
 *
 * Flow: ActivityRecognitionClient watches for IN_VEHICLE enter/exit and notifies
 * [ActivityTransitionReceiver], which relays start/stop actions here. Location
 * updates only run while IN_VEHICLE is active, saving battery the rest of the
 * time. Consecutive GPS fixes are filtered for accuracy and implausible speed
 * jumps, then accumulated distance is flushed to [CarRepository] in small
 * batches (not on every fix) to limit database writes.
 */
class DrivingTrackingService : Service() {

    private lateinit var database: AppDatabase
    private lateinit var repository: CarRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var activityRecognitionClient: ActivityRecognitionClient
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var lastAcceptedLocation: Location? = null
    private var accumulatedKm = 0.0

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach(::processLocation)
        }
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(applicationContext)
        repository = CarRepository(database.carDao())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        activityRecognitionClient = ActivityRecognition.getClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )

        when (intent?.action) {
            ACTION_START_LOCATION_UPDATES -> startLocationUpdates()
            ACTION_STOP_LOCATION_UPDATES -> stopLocationUpdates()
            ACTION_STOP_TRACKING -> {
                stopLocationUpdates()
                unregisterActivityTransitions()
                TrackingPreferences.setEnabled(applicationContext, false)
                stopSelf()
            }
            // ACTION_START_TRACKING, a system restart (null intent), or boot: (re)arm activity monitoring.
            else -> registerActivityTransitions()
        }
        return START_STICKY
    }

    private fun registerActivityTransitions() {
        if (missingPermission(Manifest.permission.ACTIVITY_RECOGNITION)) return
        val transitions = listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )
        activityRecognitionClient.requestActivityTransitionUpdates(
            ActivityTransitionRequest(transitions),
            transitionPendingIntent()
        )
    }

    private fun unregisterActivityTransitions() {
        activityRecognitionClient.removeActivityTransitionUpdates(transitionPendingIntent())
    }

    private fun transitionPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            REQUEST_CODE_TRANSITION,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun startLocationUpdates() {
        if (missingPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
        lastAcceptedLocation = null
        accumulatedKm = 0.0
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            TimeUnit.SECONDS.toMillis(LOCATION_INTERVAL_SECONDS)
        )
            .setMinUpdateDistanceMeters(MIN_UPDATE_DISTANCE_METERS)
            .build()
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        lastAcceptedLocation = null
        flushAccumulatedDistance()
    }

    private fun processLocation(location: Location) {
        if (location.accuracy > MAX_ACCEPTABLE_ACCURACY_METERS) return

        val previous = lastAcceptedLocation
        if (previous == null) {
            lastAcceptedLocation = location
            return
        }

        val distanceMeters = previous.distanceTo(location)
        val timeDeltaSeconds = (location.time - previous.time) / 1000.0
        if (timeDeltaSeconds <= 0) return

        val speedKmh = (distanceMeters / timeDeltaSeconds) * 3.6
        lastAcceptedLocation = location
        if (speedKmh > MAX_PLAUSIBLE_SPEED_KMH) {
            // Implausible GPS jump (tunnel/reflection glitch) — reset the baseline, don't count it.
            return
        }

        accumulatedKm += distanceMeters / 1000.0
        if (accumulatedKm >= MIN_KM_BEFORE_PERSIST) {
            flushAccumulatedDistance()
        }
    }

    private fun flushAccumulatedDistance() {
        if (accumulatedKm <= 0.0) return
        val distanceToPersist = accumulatedKm
        accumulatedKm = 0.0
        serviceScope.launch {
            val carId = database.carDao().getSelectedCar().first()?.id ?: return@launch
            repository.recordAutoDrivingDistance(carId, distanceToPersist)
            TrackingPreferences.setLastUpdateEpoch(applicationContext, System.currentTimeMillis())
        }
    }

    private fun missingPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED

    private fun buildNotification(): Notification {
        NotificationHelper.createTrackingChannel(this)
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, NotificationHelper.TRACKING_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.tracking_notification_title))
            .setContentText(getString(R.string.tracking_notification_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        stopLocationUpdates()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START_TRACKING = "com.example.action.START_TRACKING"
        const val ACTION_STOP_TRACKING = "com.example.action.STOP_TRACKING"
        const val ACTION_START_LOCATION_UPDATES = "com.example.action.START_LOCATION_UPDATES"
        const val ACTION_STOP_LOCATION_UPDATES = "com.example.action.STOP_LOCATION_UPDATES"

        private const val NOTIFICATION_ID = 5001
        private const val REQUEST_CODE_TRANSITION = 9001
        private const val MAX_ACCEPTABLE_ACCURACY_METERS = 30f
        private const val MIN_UPDATE_DISTANCE_METERS = 20f
        private const val LOCATION_INTERVAL_SECONDS = 15L
        private const val MAX_PLAUSIBLE_SPEED_KMH = 200.0
        private const val MIN_KM_BEFORE_PERSIST = 0.3
    }
}
