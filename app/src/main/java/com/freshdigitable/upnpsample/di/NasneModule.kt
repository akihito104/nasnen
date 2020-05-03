package com.freshdigitable.upnpsample.di

import android.app.Application
import com.freshdigitable.upnpsample.device.NasneDeviceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface NasneModule {
    companion object {
        @Provides
        @Singleton
        fun provideNasneDeviceProvider(app: Application): NasneDeviceProvider {
            return NasneDeviceProvider(app.applicationContext)
        }
    }
}
