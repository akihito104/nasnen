package com.freshdigitable.upnpsample

import android.util.Log
import net.mm2d.upnp.Device
import net.mm2d.upnp.util.asIterable
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
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
                cont.resume(RecordScheduleListResponse.create(it))
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

    companion object {
        private val TAG = RecordScheduleListResponse::class.java.simpleName

        fun create(rawRes: Map<String, String>): RecordScheduleListResponse {
            return rawRes.toList().fold(RecordScheduleListResponse()) { res, (t, u) ->
                when (t) {
                    "Result" -> {
                        val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                            .parse(InputSource(StringReader(u)))
                            .documentElement
                        res.result.items.addAll(xml.childNodes.map { createItem(it) })
                    }
                    "NumberReturned" -> res.numberReturned = u.toInt()
                    "TotalMatches" -> res.totalMatches = u.toInt()
                    "UpdateID" -> res.updateId = u
                }
                res
            }
        }

        private fun createItem(itemNode: Node): ResultItem {
            return ResultItem().apply {
                itemNode.childNodes.map { it as Element }.forEach { item ->
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
                        else -> Log.d(TAG, "unknown tag: ")
                    }
                }
            }
        }
    }
}

inline fun <R> NodeList.map (block: (Node) -> R): List<R> {
    return this.asIterable().map(block)
}
