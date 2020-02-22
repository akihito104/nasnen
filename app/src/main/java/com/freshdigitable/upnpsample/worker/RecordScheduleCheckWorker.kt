package com.freshdigitable.upnpsample.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.freshdigitable.upnpsample.R
import com.freshdigitable.upnpsample.RecordScheduleRepository
import javax.inject.Inject

class RecordScheduleCheckWorker(
    private val repository: RecordScheduleRepository,
    context: Context,
    param: WorkerParameters
) : CoroutineWorker(context, param) {
    override suspend fun doWork(): Result {
        val recordScheduleList = repository.getAllRecordScheduleItems()
        val mediaAlert = recordScheduleList
            .filter { it.hasWarnings }
            .joinToString { it.title }
        if (mediaAlert.isEmpty()) {
            return Result.success()
        }
        val notif = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notif_schedule_checker_channel_id)
        )
            .setSmallIcon(R.drawable.ic_warning_black_24dp)
            .setContentTitle("録画が失敗しそうです")
            .setContentText("notice: $mediaAlert")
            .build()
        NotificationManagerCompat.from(applicationContext)
            .notify(0, notif)
        return Result.success()
    }

    class Factory @Inject constructor(
        private val repository: RecordScheduleRepository
    ) : NasneWorkerFactory.Child {
        override fun create(
            appContext: Context,
            workerParameters: WorkerParameters
        ): ListenableWorker = RecordScheduleCheckWorker(repository, appContext, workerParameters)
    }
}
