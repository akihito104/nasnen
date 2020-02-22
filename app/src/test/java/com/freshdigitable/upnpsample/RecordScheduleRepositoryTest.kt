package com.freshdigitable.upnpsample

import com.freshdigitable.upnpsample.db.RecordScheduleDao
import com.freshdigitable.upnpsample.db.RecordScheduleItemEntity
import com.freshdigitable.upnpsample.device.NasneDevice
import com.freshdigitable.upnpsample.device.NasneDeviceProvider
import com.freshdigitable.upnpsample.device.data.RecordScheduleResultResponse
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.TimeUnit

class RecordScheduleRepositoryTest {

    @Test
    fun getAllRecordScheduleItems_daoHasEntity_andThen_returnsFromDao(): Unit = runBlocking {
        val item = mockEntity(System.currentTimeMillis() - 200)
        val dao = mockk<RecordScheduleDao>().apply {
            coEvery { getAllRecordScheduleItems() } returns listOf(item)
        }
        val sut = RecordScheduleRepository(dao, mockk())

        val actual = sut.getAllRecordScheduleItems()

        coVerify { dao.getAllRecordScheduleItems() }
        assertThat(actual).isNotEmpty()
    }

    @Test
    fun getAllRecordScheduleItems_daoHasExpiredEntity_andThen_returnsFromDevice(
    ): Unit = runBlocking {
        val time = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(100)
        val item = mockEntity(time)
        val dao = mockk<RecordScheduleDao>().apply {
            coEvery { getAllRecordScheduleItems() } returns listOf(item)
            coEvery { replaceAllScheduleItems(match { it.any { i -> i.title == "test" } }) } just runs
        }
        val nasne = mockk<NasneDevice>().apply {
            coEvery { getRecordScheduleList() } returns RecordScheduleResultResponse(
                result = mutableListOf(mockk<RecordScheduleItem>(relaxed = true).apply {
                    every { title } returns "test"
                })
            )
        }
        val deviceProvider = mockDeviceProvider(nasne)
        val sut = RecordScheduleRepository(dao, deviceProvider)

        val actual = sut.getAllRecordScheduleItems()

        coVerify { dao.getAllRecordScheduleItems() }
        coVerify { nasne.getRecordScheduleList() }
        coVerify { deviceProvider.findDevice() }
        assertThat(actual).isNotEmpty()
    }

    @Test
    fun getAllRecordScheduleItems_daoHasNoEntity_andThen_returnsFromDevice(): Unit = runBlocking {
        val dao = mockk<RecordScheduleDao>().apply {
            coEvery { getAllRecordScheduleItems() } returns emptyList()
            coEvery { replaceAllScheduleItems(match { it.any { i -> i.title == "test" } }) } just runs
        }
        val nasne = mockk<NasneDevice>().apply {
            coEvery { getRecordScheduleList() } returns RecordScheduleResultResponse(
                result = mutableListOf(mockk<RecordScheduleItem>(relaxed = true).apply {
                    every { title } returns "test"
                })
            )
        }
        val deviceProvider = mockDeviceProvider(nasne)
        val sut = RecordScheduleRepository(dao, deviceProvider)

        val actual = sut.getAllRecordScheduleItems()

        coVerify { dao.getAllRecordScheduleItems() }
        coVerify { deviceProvider.findDevice() }
        assertThat(actual).isNotEmpty()
    }

    private fun mockDeviceProvider(nasne: NasneDevice): NasneDeviceProvider {
        return mockk<NasneDeviceProvider>().apply {
            coEvery { init() } just runs
            coEvery { findDevice() } returns nasne
            coEvery { dispose() } just runs
        }
    }

    private fun mockEntity(timestamp: Long): RecordScheduleItemEntity {
        return mockk<RecordScheduleItemEntity>().apply {
            every { lastFetchTime } returns timestamp
        }
    }
}
