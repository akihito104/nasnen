package com.freshdigitable.upnpsample.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import net.mm2d.upnp.ControlPoint
import net.mm2d.upnp.ControlPointFactory
import net.mm2d.upnp.Device
import net.mm2d.upnp.util.NetworkUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NasneDeviceProvider(context: Context) {
    private val context: Context = context.applicationContext
    private var controlPoint: ControlPoint? = null

    suspend fun init() {
        controlPoint = findControlPoint()
    }

    suspend fun findDevice(): NasneDevice {
        val device = requireNotNull(controlPoint?.searchDeviceByFriendlyName("nasne"))
        return NasneDevice(device)
    }

    fun dispose() {
        controlPoint?.run {
            stop()
            terminate()
        }
    }

    private suspend fun findControlPoint(): ControlPoint? {
        var wifiLock: WifiManager.WifiLock? = null
        return suspendCoroutine { cont ->
            context.registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent?) {
                        val controlPoint = initControlPoint()
                        Log.d(TAG, "cp> $controlPoint")
                        cont.resume(controlPoint)
                        context.unregisterReceiver(this)
                        if (wifiLock?.isHeld == true) {
                            wifiLock?.release()
                        }
                    }
                },
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )

            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "hoge").apply {
                setReferenceCounted(true)
                acquire()
            }
        }
    }

    private fun initControlPoint(): ControlPoint? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = requireNotNull(connectivityManager.activeNetwork) {
            "activeNetwork is not found"
        }
        if (connectivityManager.getNetworkCapabilities(network)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            } != true
        ) {
            Log.d(TAG, "network: $network is not capable.")
            return null
        }

        return ControlPointFactory.create(
            interfaces = NetworkUtils.getAvailableInterfaces()
        ).apply {
            initialize()
            start()
        }
    }

    private suspend fun ControlPoint.searchDeviceByFriendlyName(name: String): Device {
        return suspendCoroutine { continuation ->
            addDiscoveryListener(object :
                ControlPoint.DiscoveryListener {
                override fun onDiscover(device: Device) {
                    if (device.friendlyName == name) {
                        Log.d(TAG, "onDiscover: $device, parent>${device.parent}")
                        continuation.resume(device)
                        this@searchDeviceByFriendlyName.removeDiscoveryListener(this)
                    }
                }

                override fun onLost(device: Device) {
                    Log.d(TAG, "onLost: $device")
                }
            })
            search()
        }
    }

    companion object {
        private val TAG = NasneDeviceProvider::class.java.simpleName
    }
}
