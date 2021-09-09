package tn.seif.adidaschallenge.mvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * A base fragment class for managing view binding.
 *
 * @param VB - The fragment's view binding class.
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _vb: VB? = null
    protected val vb: VB
        get() = _vb ?: throw IllegalStateException("Can't access view binding")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vb = getViewBinding(inflater, container)
        return vb.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vb = null
    }

    /**
     * Inflate the view binding for this fragment.
     *
     * @param inflater - The fragment inflater.
     * @param container - The fragment container.
     * @return - The inflated fragment's view binding instance.
     */
    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
}