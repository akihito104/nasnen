package com.freshdigitable.upnpsample.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freshdigitable.upnpsample.MainViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelProviderFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}

@MapKey
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val clazz: KClass<out ViewModel>)

class ViewModelFactory @Inject constructor(
    private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider: Provider<out ViewModel> = providers[modelClass]
            ?: find(modelClass)
            ?: throw IllegalStateException("unregistered class: $modelClass")
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }

    private fun <T : ViewModel> find(modelClass: Class<T>): Provider<out ViewModel>? {
        return providers.entries.firstOrNull { (k, _) ->
            modelClass.isAssignableFrom(k)
        }?.value
    }
}
