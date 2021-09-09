package tn.seif.adidaschallenge.ui.main

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.TransitionManager
import dagger.android.AndroidInjection
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.databinding.ActivityMainBinding
import tn.seif.adidaschallenge.mvvm.base.BaseActivity
import tn.seif.adidaschallenge.ui.background.ReviewSyncBackgroundService
import tn.seif.adidaschallenge.ui.dialogs.DialogFactory
import tn.seif.adidaschallenge.utils.NetworkListener
import javax.inject.Inject

/**
 * The main activity of the app.
 * Holds the navigation host fragment.
 * Monitors server reachability and manages the visibility of the connection lost message.
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var networkListener: NetworkListener

    @Inject
    lateinit var dialogFactory: DialogFactory

    private lateinit var defaultConstraints: ConstraintSet

    private lateinit var noInternetConstraints: ConstraintSet

    private val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

    private val currentFragment
        get() = navHostFragment.childFragmentManager.fragments[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        initConstrainSets()
        makeFullscreenImmersive()
        networkListener.networkAvailability.observe(this) {
            when (it) {
                true -> {
                    // If connection to server was established, run reviews syncing background service sync pending reviews if any.
                    ReviewSyncBackgroundService.runService(this)
                    hideNoInternetView()
                }
                false -> showNoInternetView()
            }
        }

        vb.noInternetLayout.setOnClickListener {
            dialogFactory.createOfflineInfoDialog { dialog ->
                // Making sure that the current fragment is implementing RefreshableFragment.
                (currentFragment as? RefreshableFragment)?.run {
                    refresh()
                }
                dialog.dismiss()
            }.show(supportFragmentManager, OFFLINE_DIALOG_TAG)
        }
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun initConstrainSets() {
        defaultConstraints = ConstraintSet().apply {
            clone(vb.root)
        }
        noInternetConstraints = ConstraintSet().apply {
            clone(this@MainActivity, R.layout.activity_main_no_internet)
        }
    }

    private fun showNoInternetView() {
        TransitionManager.beginDelayedTransition(vb.root)
        noInternetConstraints.applyTo(vb.root)
    }

    private fun hideNoInternetView() {
        TransitionManager.beginDelayedTransition(vb.root)
        defaultConstraints.applyTo(vb.root)
    }

    /**
     * Makes an activity fullscreen immersive (Transparent status bar)
     */
    private fun makeFullscreenImmersive() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }

    companion object {
        private const val OFFLINE_DIALOG_TAG = "OFFLINE_DIALOG"
    }
}