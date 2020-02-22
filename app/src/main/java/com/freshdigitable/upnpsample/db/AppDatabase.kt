package com.freshdigitable.upnpsample.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        RecordScheduleItemEntity::class
    ],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordScheduleDao(): RecordScheduleDao
}
