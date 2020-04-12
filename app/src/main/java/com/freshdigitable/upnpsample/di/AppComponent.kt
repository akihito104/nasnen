package com.freshdigitable.upnpsample.di

import com.freshdigitable.upnpsample.App
import com.freshdigitable.upnpsample.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        WorkerModule::class,
        DaoModule::class,
        NasneModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
    fun inject(activity: MainActivity)
}
