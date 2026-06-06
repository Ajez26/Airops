package com.airops.services

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.airops.R
import com.airops.data.repository.MatchRepository
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Foreground service that tracks the player's GPS location
 * and sends updates to the backend at regular intervals while a match is active.
 *
 * Started via Context.startForegroundService(intent) with extras:
 *   - "matchId"  (String) — required
 */
@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject lateinit var matchRepository: MatchRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var matchId: String? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            val mid = matchId ?: return
            serviceScope.launch {
                matchRepository.updateLocation(mid, loc.latitude, loc.longitude)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        matchId = intent?.getStringExtra("matchId")

        startForeground(NOTIFICATION_ID, buildNotification())

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_MS
        )
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val channelId = "airops_location"
        val manager = getSystemService(NotificationManager::class.java)

        if (manager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(
                channelId,
                "Airops Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "GPS tracking during active matches"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("AIROPS TRACKING")
            .setContentText("GPS active — reporting position")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1337
        private const val INTERVAL_MS = 5_000L
        private const val FASTEST_INTERVAL_MS = 2_000L
    }
}
