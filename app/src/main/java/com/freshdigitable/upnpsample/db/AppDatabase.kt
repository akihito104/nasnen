package com.freshdigitable.upnpsample.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RecordScheduleItemEntity::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(
    OffsetDateTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordScheduleDao(): RecordScheduleDao
}
