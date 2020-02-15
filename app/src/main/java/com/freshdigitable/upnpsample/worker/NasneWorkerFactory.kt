package com.freshdigitable.upnpsample.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class NasneWorkerFactory @Inject constructor(
    private val providers: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<Child>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return providers.entries.firstOrNull { it.key.name == workerClassName }?.value?.get()
            ?.create(appContext, workerParameters)
    }

    interface Child {
        fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker
    }
}
