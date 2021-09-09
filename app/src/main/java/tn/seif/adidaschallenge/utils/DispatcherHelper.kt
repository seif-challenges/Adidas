package tn.seif.adidaschallenge.utils

import kotlinx.coroutines.Dispatchers
import tn.seif.adidaschallenge.utils.DispatcherHelper.dispatcher

/**
 * Used to hold the default dispatcher used.
 * Makes unit testing easier: Only swapping the default [dispatcher] by a [TestCoroutineDispatcher].
 */
object DispatcherHelper {
    var dispatcher = Dispatchers.IO
}