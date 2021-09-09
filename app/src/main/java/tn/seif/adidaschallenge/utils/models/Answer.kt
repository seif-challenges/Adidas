package tn.seif.adidaschallenge.utils.models

import retrofit2.Response
import tn.seif.adidaschallenge.utils.models.Answer.Failure
import tn.seif.adidaschallenge.utils.models.Answer.Success

/**
 * A superclass presenting a state (success or failure) used to make handling API [Response] easier.
 * With typed data for success, and an exception for failure.
 *
 * @param T - The type of data that a [Success] state will hold.
 * @param E - The type of [Exception] that a [Failure] state will hold.
 */
sealed class Answer<T, E : Throwable> {
    /**
     * Presents a successful state.
     *
     * @param T - The type of success data.
     * @param E - The type of [Exception].
     * @property result - The success data of type [T]
     */
    class Success<T, E : Throwable>(val result: T) : Answer<T, E>()

    /**
     * Presents a failure state.
     *
     * @param T - The type of success data.
     * @param E - The type of [Exception].
     * @property error - The exception of type [E].
     */
    class Failure<T, E : Throwable>(val error: E) : Answer<T, E>()
}