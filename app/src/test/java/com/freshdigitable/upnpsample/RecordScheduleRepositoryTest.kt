package com.freshdigitable.upnpsample

import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.freshdigitable.upnpsample.db.RecordScheduleDao
import com.freshdigitable.upnpsample.db.RecordScheduleItemEntity
import com.freshdigitable.upnpsample.device.NasneDevice
import com.freshdigitable.upnpsample.device.NasneDeviceProvider
import com.freshdigitable.upnpsample.device.data.RecordScheduleResultResponse
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.RuleChain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@RunWith(Enclosed::class)
class RecordScheduleRepositoryTest {

    @RunWith(AndroidJUnit4::class)
    class WhenDaoHasEntity {
        @get:Rule
        val rule = RecordScheduleRepositoryTestRule()

        @Before
        fun setup(): Unit = with(rule) {
            entities.add(mockEntity(System.currentTimeMillis() - 200))
            coEvery { dao.getAllRecordScheduleItems() } returns entities
        }

        @After
        fun tearDown(): Unit = with(rule) {
            coVerify { dao.getAllRecordScheduleItems() }
        }

        @Test
        fun getAllRecordScheduleItems_returnsFromDao(): Unit = rule.runBlockingTest {
            val actual = sut.getAllRecordScheduleItems()

            assertThat(actual).isNotEmpty()
        }

        @Test
        @LooperMode(LooperMode.Mode.PAUSED)
        fun getAllRecordScheduleItemSource_returnsFromDao(): Unit = rule.runBlockingTest {
            every { dao.getAllRecordScheduleItemSource() } returns MutableLiveData(entities)

            val actual = sut.getAllRecordScheduleSource().also {
                it.observeForever { }
                looper.idle()
            }

            coVerify { dao.getAllRecordScheduleItemSource() }
            assertThat(actual.value).isNotEmpty()
        }
    }

    @RunWith(AndroidJUnit4::class)
    class WhenDaoHasExpiredEntity {
        @get:Rule
        val rule = RecordScheduleRepositoryTestRule()
        private val item = mockk<RecordScheduleItem>(relaxed = true).apply {
            every { title } returns "test"
        }
        private val nasne = mockDevice(item)

        @Before
        fun setup(): Unit = with(rule) {
            val time = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(100)
            entities.add(mockEntity(time))
            coEvery { dao.getAllRecordScheduleItems() } returns entities
            coEvery { deviceProvider.findDevice() } returns nasne
            coEvery { dao.replaceAllScheduleItems(match { it.any { i -> i.title == item.title } }) } just runs
        }

        @After
        fun tearDown(): Unit = with(rule) {
            coVerify { dao.getAllRecordScheduleItems() }
            coVerify { deviceProvider.findDevice() }
            coVerify { dao.replaceAllScheduleItems(any()) }
            coVerify { deviceProvider.init() }
            verify { deviceProvider.dispose() }
        }

        @Test
        fun getAllRecordScheduleItems_returnsFromDevice(): Unit = rule.runBlockingTest {
            val actual = sut.getAllRecordScheduleItems()

            coVerify { nasne.getRecordScheduleList() }
            assertThat(actual).isNotEmpty()
        }

        @Test
        @LooperMode(LooperMode.Mode.PAUSED)
        fun getAllRecordScheduleSource_returnsFromDevice(): Unit = rule.runBlockingTest {
            every { dao.getAllRecordScheduleItemSource() } returns MutableLiveData(entities)

            val actual = sut.getAllRecordScheduleSource().also {
                it.observeForever { }
                looper.idle()
            }

            verify { dao.getAllRecordScheduleItemSource() }
            coVerify { nasne.getRecordScheduleList() }
            assertThat(actual.value).isNotEmpty()
        }
    }

    @RunWith(AndroidJUnit4::class)
    class WhenDaoHasNoEntity {
        @get:Rule
        val rule = RecordScheduleRepositoryTestRule()
        private val item = mockk<RecordScheduleItem>(relaxed = true).apply {
            every { title } returns "test"
        }
        private val nasne = mockDevice(item)

        @Before
        fun setup(): Unit = with(rule) {
            coEvery { dao.getAllRecordScheduleItems() } returns emptyList()
            coEvery { deviceProvider.findDevice() } returns nasne
        }

        @After
        fun tearDown(): Unit = with(rule) {
            coVerify { dao.getAllRecordScheduleItems() }
            coVerify { deviceProvider.findDevice() }
            coVerify { deviceProvider.init() }
            verify { deviceProvider.dispose() }
        }

        @Test
        fun getAllRecordScheduleItems_returnsFromDevice(): Unit = rule.runBlockingTest {
            coEvery { dao.replaceAllScheduleItems(match { it.any { i -> i.title == item.title } }) } just runs

            val actual = sut.getAllRecordScheduleItems()

            coVerify { dao.replaceAllScheduleItems(any()) }
            assertThat(actual).isNotEmpty()
        }

        @Test
        @LooperMode(LooperMode.Mode.PAUSED)
        fun getAllRecordScheduleSource_returnsFromDevice(): Unit = rule.runBlockingTest {
            val itemSource = MutableLiveData<List<RecordScheduleItemEntity>>(emptyList())
            every { dao.getAllRecordScheduleItemSource() } returns itemSource
            coEvery { dao.replaceAllScheduleItems(match { it.any { i -> i.title == item.title } }) } answers {
                itemSource.value = listOf(mockEntity(System.currentTimeMillis()))
            }

            val actual = sut.getAllRecordScheduleSource().also {
                it.observeForever { }
                looper.idle()
            }

            verify { dao.getAllRecordScheduleItemSource() }
            coVerify { dao.replaceAllScheduleItems(any()) }
            assertThat(actual.value).isNotEmpty()
        }
    }

    private companion object {
        private fun mockEntity(timestamp: Long): RecordScheduleItemEntity {
            return mockk<RecordScheduleItemEntity>().apply {
                every { lastFetchTime } returns timestamp
            }
        }

        private fun mockDevice(vararg item: RecordScheduleItem): NasneDevice {
            return mockk<NasneDevice>().apply {
                coEvery { getRecordScheduleList() } returns RecordScheduleResultResponse(
                    result = mutableListOf(*item)
                )
            }
        }
    }
}

@ExperimentalCoroutinesApi
class RecordScheduleRepositoryTestRule : TestWatcher() {
    private val instantTaskRule = InstantTaskExecutorRule()
    val coroutineRule = CoroutineTestRule()

    lateinit var sut: RecordScheduleRepository
    val dao = mockk<RecordScheduleDao>()
    val deviceProvider = mockk<NasneDeviceProvider>().apply {
        coEvery { init() } just runs
        coEvery { dispose() } just runs
    }
    val entities = mutableListOf<RecordScheduleItemEntity>()
    val looper: ShadowLooper = shadowOf(getMainLooper())

    override fun starting(description: Description?) {
        super.starting(description)
        sut = RecordScheduleRepository(dao, deviceProvider, coroutineRule.coroutineContextProvider)
    }

    override fun apply(base: Statement?, description: Description?): Statement {
        return RuleChain.outerRule(instantTaskRule)
            .around(coroutineRule)
            .apply(super.apply(base, description), description)
    }

    fun runBlockingTest(block: suspend RecordScheduleRepositoryTestRule.() -> Unit) {
        return coroutineRule.runBlockingTest {
            block(this@RecordScheduleRepositoryTestRule)
        }
    }

    override fun succeeded(description: Description?) {
        super.succeeded(description)
        confirmVerified(dao, deviceProvider)
    }
}
