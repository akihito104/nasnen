package com.freshdigitable.upnpsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.freshdigitable.upnpsample.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}

@Module
interface MainActivityModule {
    @ContributesAndroidInjector
    fun contributeListFragment(): ListFragment

    @ContributesAndroidInjector
    fun contributeDetailFragment(): DetailFragment

    @Binds
    fun bindViewModelStoreOwner(mainActivity: MainActivity): ViewModelStoreOwner

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}
