package tn.seif.adidaschallenge.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import tn.seif.adidaschallenge.App
import javax.inject.Singleton

/**
 * Component used by dagger to configure the modules for the app.
 */
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        UiModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<App> {
    /**
     * Builder used by dagger to build the [AppComponent].
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
         * Function to create the AppComponent from the builder.
         *
         * @return The created Appcomponent.
         */
        fun build(): AppComponent
    }
}