package com.freshdigitable.upnpsample.di

import com.freshdigitable.upnpsample.App
import com.freshdigitable.upnpsample.device.NasneDeviceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface NasneModule {
    companion object {
        @Provides
        @Singleton
        fun provideNasneDeviceProvider(app: App): NasneDeviceProvider {
            return NasneDeviceProvider(app.applicationContext)
        }
    }
}
