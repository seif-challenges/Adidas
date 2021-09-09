package tn.seif.adidaschallenge

import dagger.android.AndroidInjector
import tn.seif.adidaschallenge.data.local.AppDatabase
import tn.seif.adidaschallenge.di.AppModule
import tn.seif.adidaschallenge.di.DaggerAppComponent
import tn.seif.adidaschallenge.di.DatabaseModule
import tn.seif.adidaschallenge.mvvm.KnownViewModels
import tn.seif.adidaschallenge.mvvm.base.BaseMvvmApp
import javax.inject.Inject

/**
 * App entry class
 */
class App : BaseMvvmApp<KnownViewModels>() {

    @Inject
    override lateinit var knownViewModels: KnownViewModels

    private val appModule by lazy { AppModule(this) }

    override fun createInjector(): AndroidInjector<out BaseMvvmApp<KnownViewModels>> {
        return DaggerAppComponent
            .builder()
            .appModule(appModule)
            .databaseModule(DatabaseModule(AppDatabase.build(this)))
            .build()
    }
}
