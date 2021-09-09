package tn.seif.adidaschallenge.mvvm

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import tn.seif.adidaschallenge.mvvm.base.BaseKnownViewModels.Companion.getViewModel
import tn.seif.adidaschallenge.mvvm.base.BaseMvvmApp
import tn.seif.adidaschallenge.mvvm.base.BaseViewModel
import kotlin.reflect.KClass

/**
 * Creates an instance of the fragment's view model.
 *
 * @param V - The type of the view model.
 * @param vmClass - The class of the view model.
 */
@Suppress("UNCHECKED_CAST")
fun <V : BaseViewModel> Fragment.viewModel(vmClass: KClass<V>) =
    ViewModelLazy(vmClass, { viewModelStore }) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return getViewModel(
                    (activity?.application as BaseMvvmApp<*>).knownViewModels,
                    vmClass
                ) as T
            }
        }
    }
