package com.freshdigitable.upnpsample.data

import android.util.Log
import com.freshdigitable.upnpsample.map
import org.w3c.dom.Element
import org.w3c.dom.Node

data class RecordScheduleItem(
    var title: String = "",
    var scheduledStartDateTime: String = "",
    var scheduledDuration: Int = 0,
    var scheduledConditionID: String = "",

    /**
     * recording TV channel code (hex decimal)
     */
    var scheduledChannelID: String = "",

    /**
     * TV channel code list broadcasting same program? (up to 4 of hex decimal)
     */
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
        get() = mediaRemainAlertID != "0" || conflictID != "0" || desiredMatchingID.isEmpty()

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