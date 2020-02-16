package com.freshdigitable.upnpsample.model

interface RecordScheduleResult<T> {
    val numberReturned: Int
    val totalMatches: Int
    val updateId: String
    val result: List<T>
}

interface RecordScheduleItem {
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
