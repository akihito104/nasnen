package com.freshdigitable.upnpsample.model

interface TitleItem {
    val title: String
    val scheduledStartDateTime: String
    val scheduledDuration: Int
    val scheduledChannelID: String
    val desiredQualityMode: String
    val genreID: String
    val reservationCreatorID: String
    val recordingFlag: String
    val recordDestinationID: String
    val recordSize: Int
    val portableRecordFile: String
    val titleProtectFlag: String
    val titleNewFlag: String
    val lastPlaybackTime: String
}
