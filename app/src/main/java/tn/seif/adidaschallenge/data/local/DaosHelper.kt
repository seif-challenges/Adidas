package tn.seif.adidaschallenge.data.local

/**
 * Attempts database operations defined in [databaseOperation].
 * In case of an exception wraps it in a [DatabaseException] and throw it.
 */
suspend fun <T> attemptDatabase(databaseOperation: suspend () -> T): T {
    return try {
        databaseOperation()
    } catch (e: Exception) {
        throw DatabaseException(e)
    }
}

/**
 * An exception wrapper for database exceptions.
 *
 * @property exception - The actual database exception.
 */
class DatabaseException(
    val exception: Exception
) : Exception("A database exception occurred.")
