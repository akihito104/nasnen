package com.freshdigitable.upnpsample

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.freshdigitable.upnpsample.di.ViewModelKey
import com.google.android.material.transition.MaterialContainerTransform
import dagger.Binds
import dagger.Module
import dagger.android.support.AndroidSupportInjection
import dagger.multibindings.IntoMap
import kotlinx.android.synthetic.main.fragment_detail.view.detail_date
import kotlinx.android.synthetic.main.fragment_detail.view.detail_title
import javax.inject.Inject

class DetailFragment : Fragment() {
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory
    private val viewModel: DetailFragmentViewModel by viewModels { viewModelProviderFactory }
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fitMode = MaterialContainerTransform.FIT_MODE_WIDTH
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            containerColor = Color.WHITE
        }
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
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.transitionName = args.sharedElementTransName
        val scheduleItem = viewModel.findScheduleItemByTitle(args.title)
        scheduleItem.observe(viewLifecycleOwner) {
            view.detail_title.text = it?.title
            view.detail_date.setListItemDatetime(it?.scheduledStartDateTime)
        }
    }
}

@Module
interface DetailFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(DetailFragmentViewModel::class)
    fun bindDetailFragmentViewModel(viewModel: DetailFragmentViewModel): ViewModel
}
