package com.freshdigitable.upnpsample.di

import android.app.Application
import com.freshdigitable.upnpsample.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppBuilder::class,
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
        fun application(app: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
