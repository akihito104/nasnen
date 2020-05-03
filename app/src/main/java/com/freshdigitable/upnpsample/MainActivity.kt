package com.freshdigitable.upnpsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshdigitable.upnpsample.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjection
import dagger.multibindings.IntoMap
import kotlinx.android.synthetic.main.activity_main.main_list
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = viewModelProvider[MainViewModel::class.java]
        val mainListAdapter = MainListAdapter()

        viewModel.allRecordScheduleItems.observe(this) {
            mainListAdapter.setItems(it)
            mainListAdapter.notifyDataSetChanged()
        }
        main_list.adapter = mainListAdapter
        main_list.layoutManager = LinearLayoutManager(this)
    }
}

@Module
interface MainActivityModule {

    @Binds
    fun bindViewModelStoreOwner(mainActivity: MainActivity): ViewModelStoreOwner

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}
