package com.freshdigitable.upnpsample.data

import android.util.Log
import com.freshdigitable.upnpsample.map
import com.freshdigitable.upnpsample.model.TitleItem
import org.w3c.dom.Element
import org.w3c.dom.Node

data class TitleItemResponse(
    override var title: String = "",
    override var scheduledStartDateTime: String = "",
    override var scheduledDuration: Int = 0,
    override var scheduledChannelID: String = "",
    override var desiredQualityMode: String = "",
    override var genreID: String = "",
    override var reservationCreatorID: String = "",
    override var recordingFlag: String = "",
    override var recordDestinationID: String = "",
    override var recordSize: Int = 0,
    override var portableRecordFile: String = "",
    override var titleProtectFlag: String = "",
    override var titleNewFlag: String = "",
    override var lastPlaybackTime: String = ""
) : TitleItem {
    companion object {
        private val TAG = RecordScheduleItemResponse::class.java.simpleName

        fun createItem(itemNode: Node): TitleItemResponse = itemNode.childNodes
            .map { it as Element }
            .fold(TitleItemResponse()) { res, item ->
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
