package com.freshdigitable.upnpsample.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RecordScheduleDao {

    @Transaction
    suspend fun replaceAllScheduleItems(items: Iterable<RecordScheduleItemEntity>) {
        deleteAllRecordScheduleItems()
        addRecordScheduleItems(items)
    }

    @Insert
    suspend fun addRecordScheduleItems(items: Iterable<RecordScheduleItemEntity>)

    @Query("DELETE FROM record_schedule")
    suspend fun deleteAllRecordScheduleItems()

    @Query("SELECT * FROM record_schedule")
    fun getAllRecordScheduleItemSource(): LiveData<List<RecordScheduleItemEntity>>

    @Query("SELECT * FROM record_schedule")
    suspend fun getAllRecordScheduleItems(): List<RecordScheduleItemEntity>
}
