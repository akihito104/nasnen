package com.freshdigitable.upnpsample

import android.util.Log
import com.freshdigitable.upnpsample.data.PvrRes
import com.freshdigitable.upnpsample.data.RecordScheduleItem
import com.freshdigitable.upnpsample.data.RecordScheduleResultResponse
import com.freshdigitable.upnpsample.data.TitleItem
import net.mm2d.upnp.Device
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NasneDevice(
    private val nasne: Device
) {
    suspend fun getRecordScheduleList(
        startingIndex: Int? = null,
        count: Int? = null
    ): RecordScheduleResultResponse<RecordScheduleItem> {
        val args = recordScheduleArgs(startingIndex, count)
        return action("X_GetRecordScheduleList", args) { res ->
            RecordScheduleResultResponse.create(res) { node -> RecordScheduleItem.createItem(node) }
        }
    }

    suspend fun getTitleList(
        startingIndex: Int? = null,
        count: Int? = null
    ): RecordScheduleResultResponse<TitleItem> {
        val args = recordScheduleArgs(startingIndex, count)
        return action("X_GetTitleList", args) { map ->
            RecordScheduleResultResponse.create(map) { node -> TitleItem.createItem(node) }
        }
    }

    private fun recordScheduleArgs(
        startingIndex: Int? = null,
        count: Int? = null
    ): Map<String, String> = mapOf(
        "SearchCriteria" to "",
        "Filter" to "",
        "StartingIndex" to (startingIndex?.toString() ?: ""),
        "RequestedCount" to (count?.toString() ?: ""),
        "SortCriteria" to ""
    )

    suspend fun getConflictList(): RecordScheduleResultResponse<RecordScheduleItem> { // わからん
        val args = mapOf(
            "Elements" to ""
        )
        return action("X_GetConflictList", args) { res ->
            RecordScheduleResultResponse.create(res) { node -> RecordScheduleItem.createItem(node) }
        }
    }

    suspend fun getLiveChList(): PvrRes {
        val args = mapOf(
            "BroadcastType" to "",
            "SkipChannel" to ""
        )
        return action("X_GetLiveChList", args) { res ->
            PvrRes.create(res)
        }
    }

    suspend fun getMediaInfo(): PvrRes { // わからん
        val args = mapOf(
            "recordDestinationID" to ""
        )
        return action("X_GetMediaInfo", args) { res ->
            PvrRes.create(res)
        }
    }

    private suspend fun <R> action(
        name: String,
        args: Map<String, String>,
        block: (Map<String, String>) -> R
    ): R = suspendCoroutine { cont ->
        requireNotNull(nasne.findAction(name)).invoke(args, onResult = { res ->
            cont.resume(block(res))
        }, onError = {
            Log.d(TAG, "error: $it")
        })
    }

    companion object {
        private val TAG: String = NasneDevice::class.java.simpleName
    }
}
