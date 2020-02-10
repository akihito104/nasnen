package com.freshdigitable.upnpsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.freshdigitable.upnpsample.data.RecordScheduleResultResponse
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workRequest =
            PeriodicWorkRequestBuilder<RecordScheduleCheckWorker>(30, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "scheduleChecker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
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
