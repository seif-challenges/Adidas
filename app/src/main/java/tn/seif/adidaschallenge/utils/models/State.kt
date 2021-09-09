package tn.seif.adidaschallenge.utils.models

import androidx.lifecycle.LiveData
import tn.seif.adidaschallenge.utils.models.State.Error
import tn.seif.adidaschallenge.utils.models.State.Success

/**
 * A super class presenting different states.
 * Can be used with as a [LiveData] data to facilitate the observation of stateful events.
 *
 * @param T - The type of data held in [Success].
 * @param E - The type of [Exception] held in [Error].
 */
sealed class State<T, E : Exception> {
    /**
     * Present a successful state.
     *
     * @param T - The type of data.
     * @param E - The type of [Exception].
     * @property result - The data held by this success state of type [T].
     */
    class Success<T, E : Exception>(val result: T) : State<T, E>()

    /**
     * Present an error state.
     *
     * @param T - The type of data.
     * @param E - The type of [Exception]
     * @property error - The instance of the exception of type [E].
     */
    class Error<T, E : Exception>(val error: E) : State<T, E>()

    /**
     * Present a loading state.
     *
     * @param T - The type of data.
     * @param E - The type of [Exception].
     */
    class Loading<T, E : Exception> : State<T, E>()
}