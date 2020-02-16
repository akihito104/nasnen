package com.freshdigitable.upnpsample.data

import android.util.Log
import com.freshdigitable.upnpsample.map
import com.freshdigitable.upnpsample.model.RecordScheduleResult
import com.freshdigitable.upnpsample.toXmlElements
import org.w3c.dom.Node

data class RecordScheduleResultResponse<T>(
    override var numberReturned: Int = 0,
    override var totalMatches: Int = 0,
    override var updateId: String = "",
    override var result: MutableList<T> = mutableListOf()
) : RecordScheduleResult<T> {

    companion object {
        private val TAG = RecordScheduleResultResponse::class.java.simpleName

        fun <T> create(
            rawRes: Map<String, String>,
            block: (Node) -> T
        ): RecordScheduleResultResponse<T> = rawRes.toList().fold(
            RecordScheduleResultResponse()
        ) { res, (t, u) ->
            res.apply {
                when (t) {
                    "Result" -> {
                        runCatching {
                            res.result.addAll(u.toXmlElements().childNodes.map(block))
                        }.onFailure {
                            Log.d(TAG, "xml: $u")
                        }
                    }
                    "NumberReturned" -> numberReturned = u.toInt()
                    "TotalMatches" -> totalMatches = u.toInt()
                    "UpdateID" -> updateId = u
                    else -> Log.d(TAG, "unknown tag> $t : $u")
                }
            }
        }
    }
}
