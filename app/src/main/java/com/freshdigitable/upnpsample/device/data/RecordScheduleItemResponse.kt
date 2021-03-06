package com.freshdigitable.upnpsample.device.data

import android.util.Log
import com.freshdigitable.upnpsample.map
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.w3c.dom.Element
import org.w3c.dom.Node

data class RecordScheduleItemResponse(
    override var title: String = "",
    override var scheduledStartDateTime: OffsetDateTime = OffsetDateTime.MIN,
    override var scheduledDuration: Int = 0,
    override var scheduledConditionID: String = "",

    /**
     * recording TV channel code (hex decimal)
     */
    override var scheduledChannelID: String = "",

    /**
     * TV channel code list broadcasting same program? (up to 4 of hex decimal)
     */
    override var desiredMatchingID: String = "",
    override var desiredQualityMode: String = "",
    override var genreID: String = "",
    override var conflictID: String = "",
    override var mediaRemainAlertID: String = "",
    override var reservationCreatorID: String = "",
    override var recordingFlag: String = "",
    override var recordDestinationID: String = "",
    override var recordSize: Int = 0,
    override var portableRecordFile: String = ""
) : RecordScheduleItem {

    companion object {
        private val TAG = RecordScheduleItemResponse::class.java.simpleName
        private val NASNE_DATE_TIME_FORMAT: DateTimeFormatter =
            DateTimeFormatterBuilder().parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .appendOffset("+HHMM", "Z")
                .toFormatter().withChronology(IsoChronology.INSTANCE)

        fun createItem(itemNode: Node): RecordScheduleItemResponse = itemNode.childNodes
            .map { it as Element }
            .fold(RecordScheduleItemResponse()) { res, item ->
                res.apply {
                    val textContent = item.textContent
                    when (item.tagName) {
                        "title" -> title = textContent
                        "scheduledStartDateTime" -> scheduledStartDateTime =
                            NASNE_DATE_TIME_FORMAT.parse(textContent, OffsetDateTime.FROM)
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
