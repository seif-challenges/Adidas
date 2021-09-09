package tn.seif.adidaschallenge.di.background

import dagger.Component
import dagger.android.AndroidInjector
import tn.seif.adidaschallenge.di.AppModule
import tn.seif.adidaschallenge.di.DatabaseModule
import tn.seif.adidaschallenge.ui.background.ReviewSyncBackgroundService
import javax.inject.Singleton

/**
 * Component used by dagger to configure the modules for the [ReviewSyncBackgroundService].
 */
@Component(modules = [AppModule::class])
@Singleton
interface BackgroundServiceComponent : AndroidInjector<ReviewSyncBackgroundService> {

    /**
     * Builder used by dagger to build the [BackgroundServiceComponent].
     */
    @Component.Builder
    interface Builder {
        /**
         * Function used by dagger to set the app module.
         *
         * @param app The app module.
         * @return This builder.
         */
        fun appModule(app: AppModule): Builder

        /**
         * Function used by dagger to se the database module.
         *
         * @param databaseModule - The database module.
         * @return - The builder
         */
        fun databaseModule(databaseModule: DatabaseModule): Builder

        /**
         * Function to create the BackgroundServiceComponent from the builder.
         *
         * @return The created BackgroundServiceComponent.
         */
        fun build(): BackgroundServiceComponent
    }
}