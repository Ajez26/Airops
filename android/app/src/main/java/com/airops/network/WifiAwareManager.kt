package com.airops.network

import android.content.Context
import android.net.wifi.aware.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WifiAwareManager — Handles Wi-Fi Aware (NAN) peer discovery and data communication.
 * This is the core offline connectivity layer for Airops.
 *
 * Flow:
 * 1. Organizer publishes a service named "airops-<matchCode>"
 * 2. Players subscribe to discover the organizer
 * 3. Peers establish NetworkSpecifier connections for data exchange
 * 4. GPS locations and game events flow over NAN without internet
 */
@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class WifiAwareManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "WifiAware"
    private var session: WifiAwareSession? = null
    private var publishDiscoverySession: PublishDiscoverySession? = null
    private var subscribeDiscoverySession: SubscribeDiscoverySession? = null

    val isSupported: Boolean
        get() = context.packageManager.hasSystemFeature("android.hardware.wifi.aware")

    /**
     * Attach to Wi-Fi Aware and prepare for session
     */
    fun attach(onReady: (WifiAwareSession) -> Unit, onFailed: () -> Unit) {
        if (!isSupported) {
            Log.w(TAG, "Wi-Fi Aware not supported on this device")
            onFailed()
            return
        }

        val wifiAwareManager = context.getSystemService(Context.WIFI_AWARE_SERVICE) as android.net.wifi.aware.WifiAwareManager
        wifiAwareManager.attach(object : AttachCallback() {
            override fun onAttached(awSession: WifiAwareSession) {
                Log.d(TAG, "Wi-Fi Aware attached")
                session = awSession
                onReady(awSession)
            }
            override fun onAttachFailed() {
                Log.e(TAG, "Wi-Fi Aware attach failed")
                onFailed()
            }
        }, null)
    }

    /**
     * Publish match service (Organizer side)
     */
    fun publish(
        matchCode: String,
        onMessageReceived: (peerId: PeerHandle, message: ByteArray) -> Unit,
        onConnected: (peerId: PeerHandle) -> Unit
    ) {
        val config = PublishConfig.Builder()
            .setServiceName("airops-$matchCode")
            .build()

        session?.publish(config, object : DiscoverySessionCallback() {
            override fun onPublishStarted(session: PublishDiscoverySession) {
                Log.d(TAG, "Publishing match: $matchCode")
                publishDiscoverySession = session
            }
            override fun onMessageReceived(peerHandle: PeerHandle, message: ByteArray) {
                onMessageReceived(peerHandle, message)
                onConnected(peerHandle)
            }
        }, null)
    }

    /**
     * Subscribe to discover match (Player side)
     */
    fun subscribe(
        matchCode: String,
        onDiscovered: (peerId: PeerHandle) -> Unit,
        onMessageReceived: (peerId: PeerHandle, message: ByteArray) -> Unit
    ) {
        val config = SubscribeConfig.Builder()
            .setServiceName("airops-$matchCode")
            .build()

        session?.subscribe(config, object : DiscoverySessionCallback() {
            override fun onServiceDiscovered(
                peerHandle: PeerHandle,
                serviceSpecificInfo: ByteArray?,
                matchFilter: MutableList<ByteArray>?
            ) {
                Log.d(TAG, "Discovered match organizer for: $matchCode")
                onDiscovered(peerHandle)
                // Send handshake
                subscribeDiscoverySession?.sendMessage(peerHandle, 0, "HELLO".toByteArray())
            }
            override fun onMessageReceived(peerHandle: PeerHandle, message: ByteArray) {
                onMessageReceived(peerHandle, message)
            }
        }, null)
    }

    /**
     * Send message to a peer
     */
    fun sendMessage(peerHandle: PeerHandle, message: ByteArray) {
        val msgId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        publishDiscoverySession?.sendMessage(peerHandle, msgId, message)
            ?: subscribeDiscoverySession?.sendMessage(peerHandle, msgId, message)
    }

    fun close() {
        publishDiscoverySession?.close()
        subscribeDiscoverySession?.close()
        session?.close()
    }
}
