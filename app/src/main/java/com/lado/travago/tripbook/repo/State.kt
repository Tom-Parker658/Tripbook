package com.lado.travago.tripbook.repo

/**
 * Contains states for a firebase processes
 */
sealed class State<T> {
    /**
     * Process In progress State
     */
    class Loading<T> : State<T>()

    /**
     * Process completed successfully
     * @param data is the response from the process
     */
    data class Success<T>(val data: T) : State<T>()

    /**
     * Process Failed
     * @param exception is the thing to handle
     */
    data class Failed<T>(val exception: Exception) : State<T>()


    /**
     * Represent the different processes states that any process can return
     * @property loading function tells the process is still in progress
     * @property success function gets called when the process finishes and returns the
     * State with the data or response
     * @property failed function gets called when the process fails and returns the state
     * with an error message
     */

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> failed(exception: Exception) = Failed<T>(exception)
    }
}