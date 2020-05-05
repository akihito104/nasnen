package com.freshdigitable.upnpsample.di

import com.freshdigitable.upnpsample.RecordScheduleRepository
import com.freshdigitable.upnpsample.db.RecordScheduleDao
import com.freshdigitable.upnpsample.device.NasneDeviceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface RepositoryModule {
    companion object {
        @Singleton
        @Provides
        fun provideRecordScheduleRepository(
            dao: RecordScheduleDao,
            deviceProvider: NasneDeviceProvider
        ): RecordScheduleRepository = RecordScheduleRepository(dao, deviceProvider)
    }
}
