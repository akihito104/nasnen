package com.freshdigitable.upnpsample

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RecordScheduleCheckWorker(
    private val context: Context,
    param: WorkerParameters
) : CoroutineWorker(context, param) {
    override suspend fun doWork(): Result {
        withDevice {
            val recordScheduleList = getRecordScheduleList()
            MainActivity.logd("recordSchedule", recordScheduleList)
            val mediaAlert = recordScheduleList.result
                .filter { it.hasWarnings }
                .joinToString { it.title }
            if (mediaAlert.isEmpty()) {
                return Result.success()
            }
            val notif = NotificationCompat.Builder(
                context,
                context.getString(R.string.notif_schedule_checker_channel_id)
            )
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setContentTitle("録画が失敗しそうです")
                .setContentText("notice: $mediaAlert")
                .build()
            NotificationManagerCompat.from(context)
                .notify(0, notif)
        }
        return Result.success()
    }

    private suspend inline fun withDevice(block: NasneDevice.() -> Unit) {
        val nasneDeviceProvider = NasneDeviceProvider(context)
        nasneDeviceProvider.init()
        val nasneDevice = nasneDeviceProvider.findDevice()
        block(nasneDevice)
        nasneDeviceProvider.dispose()
    }
}
