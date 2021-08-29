package com.lado.travago.tripbook.model.error

import com.google.firebase.*
import com.google.firebase.auth.*
import kotlin.Exception

/**
 * This is an object which will be responsible for handling errors i.e presenting the right message
 * to the user when a bug, an error, exception occurs
 * */
object ErrorHandler {

    /**
     * general error handler
     */
    fun Exception.handleError(doSomething: (exception: Exception ) -> Unit): String {
        doSomething(this)
        return when(this){
            is FirebaseException -> typeFirestore(this)
            else -> "Something went wrong"
        }
    }


    /**
     * Internally handles firestore errors
     */
    private fun typeFirestore(firebaseException: FirebaseException) =
        when (firebaseException) {
            is FirebaseApiNotAvailableException -> ""
            is FirebaseNetworkException -> "You are currently offline"
            is FirebaseTooManyRequestsException -> "Please, retry later"
            is FirebaseAuthException -> when(firebaseException){
                is FirebaseAuthInvalidUserException -> "We were not able to verify you are the one"
                is FirebaseAuthInvalidCredentialsException -> "Invalid verification code, Please try again"
                is FirebaseAuthRecentLoginRequiredException -> "You must logIn to access this page"
                else -> "We could not verify your identity, Please try again"
            }
            else -> "We are sorry! Something went wrong, please try again later."
        }
}