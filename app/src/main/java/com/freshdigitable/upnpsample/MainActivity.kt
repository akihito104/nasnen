package com.freshdigitable.upnpsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import net.mm2d.upnp.ControlPoint
import net.mm2d.upnp.ControlPointFactory
import net.mm2d.upnp.Device
import net.mm2d.upnp.util.NetworkUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private var controlPoint: ControlPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launchWhenCreated {
            val controlPoint = findControlPoint() ?: return@launchWhenCreated
            this@MainActivity.controlPoint = controlPoint
            val device = controlPoint.searchDeviceByFriendlyName("nasne")
            val nasneDevice = NasneDevice(device)
            logd("recordScheduleList", nasneDevice.getRecordScheduleList())
//            logd("getConflictList", nasneDevice.getConflictList())
            logd("getTitleList", nasneDevice.getTitleList())
//                Log.d(TAG, "getLiveChList: ${nasneDevice.getLiveChList()}")
//                Log.d(TAG, "getMediaInfo: ${nasneDevice.getMediaInfo()}")

        }

    }

    private suspend fun findControlPoint(): ControlPoint? {
        var wifiLock: WifiManager.WifiLock? = null
        return suspendCoroutine { cont ->
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val controlPoint = initControlPoint()
                    Log.d(TAG, "cp> $controlPoint")
                    cont.resume(controlPoint)
                    unregisterReceiver(this)
                    if (wifiLock?.isHeld == true) {
                        wifiLock?.release()
                    }
                }
            }, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

            val wifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "hoge").apply {
                setReferenceCounted(true)
                acquire()
            }
        }
    }

    private fun initControlPoint(): ControlPoint? {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
            addDiscoveryListener(object : ControlPoint.DiscoveryListener {
                override fun onDiscover(device: Device) {
                    if (device.friendlyName == name) {
                        Log.d(TAG, "onDiscover: $device, parent>${device.parent}")
                        continuation.resume(device)
                    }
                }

                override fun onLost(device: Device) {
                    Log.d(TAG, "onLost: $device")
                }
            })
            search()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controlPoint?.run {
            stop()
            terminate()
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        fun logd(msg: String, res: RecordScheduleResultResponse<*>) {
            Log.d(TAG, "$msg: num>${res.numberReturned}, total>${res.totalMatches}")
            res.result.forEach {
                Log.d(TAG, "$it")
            }
        }
    }
}
