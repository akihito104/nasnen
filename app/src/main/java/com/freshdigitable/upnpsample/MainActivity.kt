package com.freshdigitable.upnpsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nasneDeviceProvider = NasneDeviceProvider(this)
        lifecycleScope.launchWhenCreated {
            nasneDeviceProvider.init()
            val nasneDevice = nasneDeviceProvider.findDevice()
            logd("recordScheduleList", nasneDevice.getRecordScheduleList())
            logd("getTitleList", nasneDevice.getTitleList())
            nasneDeviceProvider.dispose()

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
