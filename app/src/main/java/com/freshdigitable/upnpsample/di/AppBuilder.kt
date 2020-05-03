package com.freshdigitable.upnpsample.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.freshdigitable.upnpsample.MainActivity
import com.freshdigitable.upnpsample.MainActivityModule
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention
annotation class ActivityScope

@Module
interface AppBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    fun contributeMainActivity(): MainActivity

    companion object {
        @Provides
        fun provideViewModelProvider(
            viewModelStoreOwner: ViewModelStoreOwner,
            viewModelProviderFactory: ViewModelProvider.Factory
        ): ViewModelProvider {
            return ViewModelProvider(viewModelStoreOwner, viewModelProviderFactory)
        }
    }
}
