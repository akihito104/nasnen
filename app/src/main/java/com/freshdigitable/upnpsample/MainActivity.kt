package com.freshdigitable.upnpsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.main_list
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProvider(this, viewModelProvider)[MainViewModel::class.java]
        val mainListAdapter = MainListAdapter()

        viewModel.allRecordScheduleItems.observe(this) {
            mainListAdapter.setItems(it)
            mainListAdapter.notifyDataSetChanged()
        }
        main_list.adapter = mainListAdapter
        main_list.layoutManager = LinearLayoutManager(this)
    }
}
