package com.freshdigitable.upnpsample

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class App : Application(), Configuration.Provider {
    companion object {
        private const val WORKER_NAME_SCHEDULE_CHECKER = "scheduleChecker"
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    getString(R.string.notif_schedule_checker_channel_id),
                    getString(R.string.notif_schedule_checker_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.notif_schedule_checker_description)
                }
            NotificationManagerCompat.from(this)
                .createNotificationChannel(notificationChannel)
        }

        val workManager = WorkManager.getInstance(this)
        workManager.pruneWork()
        val workRequest =
            PeriodicWorkRequestBuilder<RecordScheduleCheckWorker>(30, TimeUnit.MINUTES)
                .build()
        workManager.enqueueUniquePeriodicWork(
            WORKER_NAME_SCHEDULE_CHECKER,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker? {
                return RecordScheduleCheckWorker(appContext, workerParameters)
            }
        })
            .build()
    }
}
