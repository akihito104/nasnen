package com.freshdigitable.upnpsample.di

import android.app.Application
import androidx.room.Room
import com.freshdigitable.upnpsample.db.AppDatabase
import com.freshdigitable.upnpsample.db.RecordScheduleDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DaoModule {
    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(app: Application): AppDatabase {
            return Room.databaseBuilder(
                app.applicationContext,
                AppDatabase::class.java,
                "nasne"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        fun provideRecordScheduleDao(db: AppDatabase): RecordScheduleDao = db.recordScheduleDao()
    }
}
