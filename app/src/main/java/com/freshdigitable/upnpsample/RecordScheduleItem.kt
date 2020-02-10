package com.freshdigitable.upnpsample

import android.util.Log
import org.w3c.dom.Element
import org.w3c.dom.Node

data class RecordScheduleItem(
    var title: String = "",
    var scheduledStartDateTime: String = "",
    var scheduledDuration: Int = 0,
    var scheduledConditionID: String = "",
    var scheduledChannelID: String = "",
    var desiredMatchingID: String = "",
    var desiredQualityMode: String = "",
    var genreID: String = "",
    var conflictID: String = "",
    var mediaRemainAlertID: String = "",
    var reservationCreatorID: String = "",
    var recordingFlag: String = "",
    var recordDestinationID: String = "",
    var recordSize: Int = 0,
    var portableRecordFile: String = ""
) {
    val hasWarnings: Boolean
        get () = mediaRemainAlertID.isNotEmpty() || conflictID.isNotEmpty()

    companion object {
        private val TAG = RecordScheduleItem::class.java.simpleName

        fun createItem(itemNode: Node): RecordScheduleItem = itemNode.childNodes
            .map { it as Element }
            .fold(RecordScheduleItem()) { res, item ->
                res.apply {
                    val textContent = item.textContent
                    when (item.tagName) {
                        "title" -> title = textContent
                        "scheduledStartDateTime" -> scheduledStartDateTime = textContent
                        "scheduledDuration" -> scheduledDuration = textContent.toInt()
                        "scheduledConditionID" -> scheduledConditionID = textContent
                        "scheduledChannelID" -> scheduledChannelID = textContent
                        "desiredMatchingID" -> desiredMatchingID = textContent
                        "desiredQualityMode" -> desiredQualityMode = textContent
                        "genreID" -> genreID = textContent
                        "conflictID" -> conflictID = textContent
                        "mediaRemainAlertID" -> mediaRemainAlertID = textContent
                        "reservationCreatorID" -> reservationCreatorID = textContent
                        "recordingFlag" -> recordingFlag = textContent
                        "recordDestinationID" -> recordDestinationID = textContent
                        "recordSize" -> recordSize = textContent.toInt()
                        "portableRecordFile" -> portableRecordFile = textContent
                        else -> Log.d(TAG, "unknown tag> ${item.tagName} : $textContent")
                    }
                }
            }
    }
}
