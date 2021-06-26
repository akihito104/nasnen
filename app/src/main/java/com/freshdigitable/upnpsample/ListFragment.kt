package com.freshdigitable.upnpsample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshdigitable.upnpsample.di.ViewModelKey
import com.google.android.material.transition.Hold
import dagger.Binds
import dagger.Module
import dagger.android.support.AndroidSupportInjection
import dagger.multibindings.IntoMap
import kotlinx.android.synthetic.main.fragment_list.view.main_list
import javax.inject.Inject

class ListFragment : Fragment() {
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory
    private val viewModel: ListFragmentViewModel by viewModels { viewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // WORKAROUND: for sharedElementReturnTransition with Navigation component
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val mainListAdapter = MainListAdapter()

        viewModel.allRecordScheduleItems.observe(viewLifecycleOwner) {
            mainListAdapter.setItems(it)
            mainListAdapter.notifyDataSetChanged()
        }
        view.main_list.adapter = mainListAdapter
        view.main_list.layoutManager = LinearLayoutManager(requireContext())
    }
}

@Module
interface ListFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(ListFragmentViewModel::class)
    fun bindListFragment(viewModel: ListFragmentViewModel): ViewModel
}
