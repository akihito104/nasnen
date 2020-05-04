package com.freshdigitable.upnpsample

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.freshdigitable.upnpsample.db.RecordScheduleDao
import com.freshdigitable.upnpsample.db.toEntity
import com.freshdigitable.upnpsample.device.NasneDeviceProvider
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import java.util.concurrent.TimeUnit

class RecordScheduleRepository(
    private val dao: RecordScheduleDao,
    private val deviceProvider: NasneDeviceProvider,
    private val coroutineContextProvider: CoroutineContextProvider = CoroutineContextProvider()
) {
    fun getAllRecordScheduleSource(): LiveData<List<RecordScheduleItem>> {
        return liveData(coroutineContextProvider.mainContext) {
            val source = dao.getAllRecordScheduleItemSource().map {
                it.map { i -> i as RecordScheduleItem }
            }
            emitSource(source)
            getAllRecordScheduleItems()
        }
    }

    suspend fun getAllRecordScheduleItems(): List<RecordScheduleItem> {
        val items = dao.getAllRecordScheduleItems()
        val expiredTimeMillis = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        return if (items.isEmpty() || items.any { it.lastFetchTime < expiredTimeMillis }) {
            fetchRecordScheduleList()
        } else {
            items
        }
    }

    private suspend fun fetchRecordScheduleList(): List<RecordScheduleItem> {
        return try {
            deviceProvider.init()
            val device = deviceProvider.findDevice()
            device.getRecordScheduleList().result.also { items ->
                val time = System.currentTimeMillis()
                dao.replaceAllScheduleItems(items.map { it.toEntity(time) })
            }
        } finally {
            deviceProvider.dispose()
        }
    }

    fun findScheduleItemByTitle(title: String): LiveData<RecordScheduleItem?> {
        return dao.findScheduleItemByTitle(title).map { it as? RecordScheduleItem }
    }
}
