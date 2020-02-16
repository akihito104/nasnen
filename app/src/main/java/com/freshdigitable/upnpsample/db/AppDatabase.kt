package com.freshdigitable.upnpsample.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.freshdigitable.upnpsample.model.RecordScheduleItem

@Database(
    entities = [
        RecordScheduleItemEntity::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordScheduleDao(): RecordScheduleDao
}

@Entity(tableName = "record_schedule")
data class RecordScheduleItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "title")
    override val title: String,

    @ColumnInfo(name = "start_datetime")
    override val scheduledStartDateTime: String,

    @ColumnInfo(name = "duration")
    override val scheduledDuration: Int,

    @ColumnInfo(name = "condition_id")
    override val scheduledConditionID: String,

    @ColumnInfo(name = "channel_id")
    override val scheduledChannelID: String,

    @ColumnInfo(name = "desired_matching_id")
    override val desiredMatchingID: String,

    @ColumnInfo(name = "desired_quality_mode")
    override val desiredQualityMode: String,

    @ColumnInfo(name = "genre_id")
    override val genreID: String,

    @ColumnInfo(name = "conflict_id")
    override val conflictID: String,

    @ColumnInfo(name = "media_remain_alert_id")
    override val mediaRemainAlertID: String,

    @ColumnInfo(name = "reservation_creator_id")
    override val reservationCreatorID: String,

    @ColumnInfo(name = "recording_flag")
    override val recordingFlag: String,

    @ColumnInfo(name = "record_destination_id")
    override val recordDestinationID: String,

    @ColumnInfo(name = "record_size")
    override val recordSize: Int,

    @ColumnInfo(name = "portable_record_file")
    override val portableRecordFile: String
) : RecordScheduleItem

@Dao
interface RecordScheduleDao {
    @Insert
    fun addRecordScheduleItems(items: Iterable<RecordScheduleItemEntity>)

    @Query("DELETE FROM record_schedule")
    fun deleteAllRecordScheduleItems()

    @Query("SELECT * FROM record_schedule")
    fun getAllRecordScheduleItems() : List<RecordScheduleItemEntity>
}
