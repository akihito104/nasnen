package com.freshdigitable.upnpsample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.view.detail_date
import kotlinx.android.synthetic.main.fragment_detail.view.detail_title
import javax.inject.Inject

class DetailFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = viewModelProvider[MainViewModel::class.java]
        val scheduleItem = viewModel.findScheduleItemByTitle(title)
        scheduleItem.observe(viewLifecycleOwner) {
            view.detail_title.text = it?.title
            view.detail_date.setListItemDatetime(it?.scheduledStartDateTime)
        }
    }

    private val title: String
        get() = requireNotNull(requireArguments().getString("title"))

    companion object {
        fun createArgument(title: String): Bundle = bundleOf(
            "title" to title
        )
    }
}
