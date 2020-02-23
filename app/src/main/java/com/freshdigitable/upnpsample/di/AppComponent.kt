package com.freshdigitable.upnpsample.di

import com.freshdigitable.upnpsample.App
import com.freshdigitable.upnpsample.CoroutineContextProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        WorkerModule::class,
        DaoModule::class,
        NasneModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: App): Builder

        @BindsInstance
        fun coroutineContextProvider(coroutineContext: CoroutineContextProvider): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
