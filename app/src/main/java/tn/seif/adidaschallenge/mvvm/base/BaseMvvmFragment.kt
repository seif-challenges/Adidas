package tn.seif.adidaschallenge.mvvm.base

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * A [Fragment] superclass, extending [BaseFragment], holding a reference of the fragment's view mdoel.
 *
 * @param VB - The type of the fragment's view binding.
 * @param VM - The type of the fragment's view model.
 */
abstract class BaseMvvmFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB>() {
    protected abstract val vm: VM
}