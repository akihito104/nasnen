package com.freshdigitable.upnpsample.model

import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder

interface RecordScheduleResult<T> {
    val numberReturned: Int
    val totalMatches: Int
    val updateId: String
    val result: List<T>
}

interface RecordScheduleItem {
    companion object {
        val NASNE_DATE_TIME_FORMAT: DateTimeFormatter = DateTimeFormatterBuilder().parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .appendOffset("+HHMM", "Z")
            .toFormatter().withChronology(IsoChronology.INSTANCE)
    }

    val title: String
    val scheduledStartDateTime: String
    val scheduledDuration: Int
    val scheduledConditionID: String

    /**
     * recording TV channel code (hex decimal)
     */
    val scheduledChannelID: String

    /**
     * TV channel code list broadcasting same program? (up to 4 of hex decimal)
     */
    val desiredMatchingID: String
    val desiredQualityMode: String
    val genreID: String
    val conflictID: String
    val mediaRemainAlertID: String
    val reservationCreatorID: String
    val recordingFlag: String
    val recordDestinationID: String
    val recordSize: Int
    val portableRecordFile: String

    val hasWarnings: Boolean
        get() = mediaRemainAlertID != "0" || conflictID != "0" || desiredMatchingID.isEmpty()
}
