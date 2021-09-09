package tn.seif.adidaschallenge.mvvm.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * A base activity class for managing view binding.
 *
 * @param VB - The activity's view binding class.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _vb: VB? = null
    protected val vb: VB
        get() = _vb ?: throw IllegalStateException("Can't access view binding.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _vb = getViewBinding()
        setContentView(vb.root)
    }

    /**
     * Inflate the view binding for this activity.
     *
     * @return - The inflated activity's view binding instance.
     */
    abstract fun getViewBinding(): VB
}