package com.freshdigitable.upnpsample

import android.util.Log
import net.mm2d.upnp.Device
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NasneDevice(
    private val nasne: Device
) {
    suspend fun getRecordScheduleList(): RecordScheduleListResponse {
        val args = mapOf(
            "SearchCriteria" to "",
            "Filter" to "",
            "StartingIndex" to "",
            "RequestedCount" to "",
            "SortCriteria" to ""
        )
        val action = requireNotNull(nasne.findAction("X_GetRecordScheduleList"))
        return suspendCoroutine { cont ->
            action.invoke(args, onResult = {
                cont.resume(it.toList().fold(RecordScheduleListResponse()) { res, (t, u) ->
                    when (t) {
                        "Result" -> {
                            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                .parse(InputSource(StringReader(u)))
                                .documentElement
                            res.result.items.addAll((0 until xml.childNodes.length).map { i ->
                                val node = xml.childNodes.item(i)
//                                Log.d(TAG, "node> ${node.nodeName}: ${node.nodeValue}")
                                RecordScheduleListResponse.ResultItem().also { resultItem ->
                                    (0 until node.childNodes.length).forEach { j ->
                                        val item = node.childNodes.item(j) as Element
                                        when (item.tagName) {
                                            "title" -> resultItem.title = item.textContent
                                            "scheduledStartDateTime" -> resultItem.scheduledStartDateTime =
                                                item.textContent
                                            "scheduledDuration" -> resultItem.scheduledDuration =
                                                item.textContent.toInt()
                                            "scheduledConditionID" -> resultItem.scheduledConditionID =
                                                item.textContent
                                            "scheduledChannelID" -> resultItem.scheduledChannelID =
                                                item.textContent
                                            "desiredMatchingID" -> resultItem.desiredMatchingID =
                                                item.textContent
                                            "desiredQualityMode" -> resultItem.desiredQualityMode =
                                                item.textContent
                                            "genreID" -> resultItem.genreID = item.textContent
                                            "conflictID" -> resultItem.conflictID =
                                                item.textContent
                                            "mediaRemainAlertID" -> resultItem.mediaRemainAlertID =
                                                item.textContent
                                            "reservationCreatorID" -> resultItem.reservationCreatorID
                                            "recordingFlag" -> resultItem.recordingFlag =
                                                item.textContent
                                            "recordDestinationID" -> resultItem.recordDestinationID =
                                                item.textContent
                                            "recordSize" -> resultItem.recordSize =
                                                item.textContent.toInt()
                                            "portableRecordFile" -> resultItem.portableRecordFile =
                                                item.textContent
                                            else -> Log.d(TAG, "unknown tag: ")
                                        }
                                    }
                                }
                            })
                        }
                        "NumberReturned" -> res.numberReturned = u.toInt()
                        "TotalMatches" -> res.totalMatches = u.toInt()
                        "UpdateID" -> res.updateId = u
                    }
                    res
                })
            })
        }
    }

    companion object {
        private val TAG: String = NasneDevice::class.java.simpleName
    }
}

data class RecordScheduleListResponse(
    var numberReturned: Int = 0,
    var totalMatches: Int = 0,
    var updateId: String = "",
    var result: RecordScheduleList = RecordScheduleList()
) {
    data class RecordScheduleList(
        var items: MutableList<ResultItem> = mutableListOf()
    )

    data class ResultItem(
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
    )
}