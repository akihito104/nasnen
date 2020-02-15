package com.freshdigitable.upnpsample.di

import androidx.work.Configuration
import androidx.work.ListenableWorker
import com.freshdigitable.upnpsample.worker.NasneWorkerFactory
import com.freshdigitable.upnpsample.worker.RecordScheduleCheckWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
interface WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(RecordScheduleCheckWorker::class)
    fun bindRecordScheduleCheckWorker(
        worker: RecordScheduleCheckWorker.Factory
    ): NasneWorkerFactory.Child

    companion object {
        @Provides
        fun provideWorkerConfiguration(workerFactory: NasneWorkerFactory): Configuration {
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }
    }
}

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class WorkerKey(val clz: KClass<out ListenableWorker>)
