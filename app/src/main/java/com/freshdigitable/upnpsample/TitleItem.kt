package com.freshdigitable.upnpsample

import android.util.Log
import org.w3c.dom.Element
import org.w3c.dom.Node

data class TitleItem(
    var title: String = "",
    var scheduledStartDateTime: String = "",
    var scheduledDuration: Int = 0,
    var scheduledChannelID: String = "",
    var desiredQualityMode: String = "",
    var genreID: String = "",
    var reservationCreatorID: String = "",
    var recordingFlag: String = "",
    var recordDestinationID: String = "",
    var recordSize: Int = 0,
    var portableRecordFile: String = "",
    var titleProtectFlag: String = "",
    var titleNewFlag: String = "",
    var lastPlaybackTime: String = ""
) {
    companion object {
        private val TAG = RecordScheduleItem::class.java.simpleName

        fun createItem(itemNode: Node): TitleItem = itemNode.childNodes
            .map { it as Element }
            .fold(TitleItem()) { res, item ->
                res.apply {
                    val textContent = item.textContent
                    when (item.tagName) {
                        "title" -> title = textContent
                        "scheduledStartDateTime" -> scheduledStartDateTime = textContent
                        "scheduledDuration" -> scheduledDuration = textContent.toInt()
                        "scheduledChannelID" -> scheduledChannelID = textContent
                        "desiredQualityMode" -> desiredQualityMode = textContent
                        "genreID" -> genreID = textContent
                        "reservationCreatorID" -> reservationCreatorID = textContent
                        "recordingFlag" -> recordingFlag = textContent
                        "recordDestinationID" -> recordDestinationID = textContent
                        "recordSize" -> recordSize = textContent.toInt()
                        "portableRecordFile" -> portableRecordFile = textContent
                        "titleProtectFlag" -> titleProtectFlag = textContent
                        "titleNewFlag" -> titleNewFlag = textContent
                        "lastPlaybackTime" -> lastPlaybackTime = textContent
                        else -> Log.d(
                            TAG,
                            "unknown tag> ${item.tagName} : $textContent"
                        )
                    }
                }
            }
    }
}
