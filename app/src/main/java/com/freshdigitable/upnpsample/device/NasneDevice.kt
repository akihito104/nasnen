package com.freshdigitable.upnpsample.device

import android.util.Log
import com.freshdigitable.upnpsample.device.data.PvrRes
import com.freshdigitable.upnpsample.device.data.RecordScheduleItemResponse
import com.freshdigitable.upnpsample.device.data.RecordScheduleResultResponse
import com.freshdigitable.upnpsample.device.data.TitleItemResponse
import com.freshdigitable.upnpsample.model.Pvr
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import com.freshdigitable.upnpsample.model.RecordScheduleResult
import com.freshdigitable.upnpsample.model.TitleItem
import net.mm2d.upnp.Device
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NasneDevice(
    private val nasne: Device
) {
    suspend fun getRecordScheduleList(
        startingIndex: Int? = null,
        count: Int? = null
    ): RecordScheduleResult<RecordScheduleItem> {
        val args = recordScheduleArgs(startingIndex, count)
        return action("X_GetRecordScheduleList", args) { res ->
            RecordScheduleResultResponse.create(res) { node -> RecordScheduleItemResponse.createItem(node) }
        }
    }

    suspend fun getTitleList(
        startingIndex: Int? = null,
        count: Int? = null
    ): RecordScheduleResult<TitleItem> {
        val args = recordScheduleArgs(startingIndex, count)
        return action("X_GetTitleList", args) { map ->
            RecordScheduleResultResponse.create(map) { node -> TitleItemResponse.createItem(node) }
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

    suspend fun getConflictList(): RecordScheduleResult<RecordScheduleItem> { // わからん
        val args = mapOf(
            "Elements" to ""
        )
        return action("X_GetConflictList", args) { res ->
            RecordScheduleResultResponse.create(res) { node -> RecordScheduleItemResponse.createItem(node) }
        }
    }

    suspend fun getLiveChList(): Pvr {
        val args = mapOf(
            "BroadcastType" to "",
            "SkipChannel" to ""
        )
        return action("X_GetLiveChList", args) { res ->
            PvrRes.create(res)
        }
    }

    suspend fun getMediaInfo(): Pvr { // わからん
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
