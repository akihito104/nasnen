package com.freshdigitable.upnpsample

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.freshdigitable.upnpsample.di.DaggerAppComponent
import com.freshdigitable.upnpsample.worker.RecordScheduleCheckWorker
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class App : Application(), Configuration.Provider, HasAndroidInjector {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)

        setupNotificationChannel()
        setupPeriodicWorker()
    }

    private fun setupNotificationChannel() {
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
    }

    @Inject
    lateinit var workerConfig: Configuration

    override fun getWorkManagerConfiguration(): Configuration = workerConfig

    private fun setupPeriodicWorker() {
        val workManager = WorkManager.getInstance(this)
        workManager.pruneWork()
        val workRequest =
            PeriodicWorkRequestBuilder<RecordScheduleCheckWorker>(30, TimeUnit.MINUTES)
                .build()
        workManager.enqueueUniquePeriodicWork(
            WORKER_NAME_SCHEDULE_CHECKER,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {
        private const val WORKER_NAME_SCHEDULE_CHECKER = "scheduleChecker"
    }
}
