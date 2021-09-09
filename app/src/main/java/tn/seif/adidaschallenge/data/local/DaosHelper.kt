package tn.seif.adidaschallenge.data.local

import tn.seif.adidaschallenge.utils.models.Answer

suspend fun <T> attemptDatabase(databaseOperation: suspend () -> T): T {
    return try {
        databaseOperation()
    } catch (e: Exception) {
        throw DatabaseException(e)
    }
}

suspend fun <T> attemptDatabaseForAnswer(databaseOperation: suspend () -> T): Answer<T, Exception> {
    return try {
        Answer.Success(databaseOperation())
    } catch (e: Exception) {
        Answer.Failure(DatabaseException(e))
    }
}

class DatabaseException(
    val exception: Exception
) : Exception("A database exception occurred.")