package tn.seif.adidaschallenge.di

import android.app.Application
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import tn.seif.adidaschallenge.App
import tn.seif.adidaschallenge.utils.ErrorHandler
import tn.seif.adidaschallenge.utils.NetworkListener
import javax.inject.Singleton

/**
 * Provides app-wide dependencies.
 */
@Module(includes = [
    DatabaseModule::class,
    NetworkModule::class
])
class AppModule(app: App) {
    private val application: Application

    init {
        application = app
    }

    /**
     * Function to provide the application to Dagger.
     */
    @Provides
    fun provideApplication(): Application {
        return application
    }

    /**
     * Function to provide an instance of [NetworkListener] to Dagger.
     */
    @Provides
    @Singleton
    fun provideNetworkListener(
        application: Application,
        okHttpClient: OkHttpClient
    ): NetworkListener {
        return NetworkListener(application, okHttpClient)
    }

    /**
     * Function to provide an instance of [ErrorHandler] to Dagger.
     */
    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }
}