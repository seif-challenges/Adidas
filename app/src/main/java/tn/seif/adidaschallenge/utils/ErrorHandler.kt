package tn.seif.adidaschallenge.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Responsible of handling errors.
 */
open class ErrorHandler {

    /**
     * Logs the exception using [Timber], and sens a report to Firebase Crashlytics.
     *
     * @param exception
     */
    open fun handle(exception: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(exception)
        Timber.e(exception)
    }
}