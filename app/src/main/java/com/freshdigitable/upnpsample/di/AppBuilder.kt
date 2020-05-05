package com.freshdigitable.upnpsample.di

import com.freshdigitable.upnpsample.MainActivity
import com.freshdigitable.upnpsample.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention
annotation class ActivityScope

@Scope
@MustBeDocumented
@Retention
annotation class FragmentScope

@Module
interface AppBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    fun contributeMainActivity(): MainActivity
}
