package com.freshdigitable.upnpsample

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.freshdigitable.upnpsample.db.RecordScheduleDao
import com.freshdigitable.upnpsample.db.toEntity
import com.freshdigitable.upnpsample.device.NasneDeviceProvider
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecordScheduleRepository @Inject constructor(
    private val dao: RecordScheduleDao,
    private val deviceProvider: NasneDeviceProvider
) {
    fun getAllRecordScheduleSource(): LiveData<List<RecordScheduleItem>> {
        return liveData {
            val source =
                dao.getAllRecordScheduleItemSource().map { it.map { i -> i as RecordScheduleItem } }
            emitSource(source)
            getAllRecordScheduleItems()
        }
    }

    suspend fun getAllRecordScheduleItems(): List<RecordScheduleItem> {
        val items = dao.getAllRecordScheduleItems()
        val currentTimeMillis = System.currentTimeMillis()
        return if (items.isEmpty() ||
            items.any { currentTimeMillis - it.lastFetchTime > TimeUnit.MINUTES.toMillis(30) }
        ) {
            fetchRecordScheduleList()
        } else {
            items
        }
    }

    private suspend fun fetchRecordScheduleList(): List<RecordScheduleItem> {
        val device = deviceProvider.findDevice()
        val recordScheduleList = device.getRecordScheduleList()
        val time = System.currentTimeMillis()
        dao.replaceAllScheduleItems(recordScheduleList.result.map { it.toEntity(time) })
        return recordScheduleList.result
    }
}
