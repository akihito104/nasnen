package com.freshdigitable.upnpsample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_list.view.main_list
import javax.inject.Inject

class ListFragment : Fragment() {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = viewModelProvider[MainViewModel::class.java]
        val mainListAdapter = MainListAdapter(object : MainListAdapter.OnItemClickListener {
            override fun onItemClicked(title: String) {
                findNavController().navigate(
                    R.id.action_main_list_to_main_detail, DetailFragment.createArgument(title)
                )
            }
        })

        viewModel.allRecordScheduleItems.observe(viewLifecycleOwner) {
            mainListAdapter.setItems(it)
            mainListAdapter.notifyDataSetChanged()
        }
        view.main_list.adapter = mainListAdapter
        view.main_list.layoutManager = LinearLayoutManager(requireContext())
    }
}
