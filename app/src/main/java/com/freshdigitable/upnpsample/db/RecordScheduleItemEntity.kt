package com.freshdigitable.upnpsample.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@Entity(tableName = "record_schedule")
data class RecordScheduleItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "title")
    override val title: String,

    @ColumnInfo(name = "start_datetime")
    override val scheduledStartDateTime: OffsetDateTime,

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
    override val portableRecordFile: String,

    @ColumnInfo(name = "last_fetch_time")
    val lastFetchTime: Long
) : RecordScheduleItem

fun RecordScheduleItem.toEntity(timestampMillis: Long): RecordScheduleItemEntity {
    return RecordScheduleItemEntity(
        title = title,
        scheduledStartDateTime = scheduledStartDateTime,
        scheduledDuration = scheduledDuration,
        scheduledConditionID = scheduledConditionID,
        scheduledChannelID = scheduledChannelID,
        desiredMatchingID = desiredMatchingID,
        desiredQualityMode = desiredQualityMode,
        genreID = genreID,
        conflictID = conflictID,
        mediaRemainAlertID = mediaRemainAlertID,
        reservationCreatorID = reservationCreatorID,
        recordingFlag = recordingFlag,
        recordDestinationID = recordDestinationID,
        recordSize = recordSize,
        portableRecordFile = portableRecordFile,
        lastFetchTime = timestampMillis
    )
}

object OffsetDateTimeConverter {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toInstant(value: String): OffsetDateTime = formatter.parse(value, OffsetDateTime.FROM)

    @TypeConverter
    @JvmStatic
    fun toDatetimeString(value: OffsetDateTime): String = formatter.format(value)
}
