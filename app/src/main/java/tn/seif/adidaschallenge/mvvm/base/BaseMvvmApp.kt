package tn.seif.adidaschallenge.mvvm.base

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import timber.log.Timber.DebugTree
import tn.seif.adidaschallenge.BuildConfig
import javax.inject.Inject

/**
 * An [Application] superclass responsible for Dagger injection and [BaseKnownViewModels] instantiation.
 *
 * @param KV - The type of the subclass of [BaseKnownViewModels] used by the app.
 */
abstract class BaseMvvmApp<KV : BaseKnownViewModels> : Application(), HasAndroidInjector {

    abstract var knownViewModels: KV

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return injector
    }

    /* region abstract functions */

    /**
     * Creates the Dagger [AndroidInjector].
     */
    abstract fun createInjector(): AndroidInjector<out BaseMvvmApp<KV>>

    /* endregion */

    /* region lifecycle */

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber if app is in debug mode.
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        inject()
    }

    /* endregion */

    /* region private functions */

    @Suppress("UNCHECKED_CAST")
    private fun inject() {
        val injector = createInjector() as AndroidInjector<Any>
        injector.inject(this)
    }

    /* endregion */
}