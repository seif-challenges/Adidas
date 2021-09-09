package tn.seif.adidaschallenge.data.remote

import retrofit2.Response
import tn.seif.adidaschallenge.utils.models.Answer

private const val STATUS_OK = 200
private const val STATUS_CREATED = 201

/**
 * Parses the server response to an [Answer].
 *
 * @return - An instance of [Answer.Success] containing the body of the response as [T] if successful.
 * Else an instance of [Answer.Failure] containing an instance of [ServerResponseException].
 */
fun <T> Response<T>.requestAnswer(): Answer<T, Exception> {
    return if (isSuccessful && body() != null && code() == STATUS_OK || code() == STATUS_CREATED) {
        Answer.Success(body()!!)
    } else {
        Answer.Failure(ServerResponseException(code(), errorBody()?.string() ?: ""))
    }
}

/**
 * Exception for handling server failure responses.
 *
 * @param errorCode - The code of the error.
 * @param responseBody - The body of the error.
 */
class ServerResponseException(
    errorCode: Int,
    responseBody: String
) : Exception("Error response from server with code: $errorCode:\n\n$responseBody")
