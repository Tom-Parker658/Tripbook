package com.lado.travago.transpido.repo.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.lado.travago.transpido.repo.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/**
 * Performs the signIn process to firebase
 * @property firebaseAuth an application instance of the authentication. Used to sign upt
 * users to firebase
 */
@ExperimentalCoroutinesApi
class FirebaseAuthRepo {
    //Initialising and getting instances of the firebase services
    private val firebaseAuth = FirebaseAuth.getInstance()

    /**
     * Uses the phone credentials to signIn a user to firebaseAuth asynchronously
     * @param credential are the generated from phoneNumber see [PhoneAuthCredential]
     * @return FirebaseUser which the signIn scanner(traveller, scanner etc) see [FirebaseUser]
     */
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential)= flow {
        firebaseAuth.useAppLanguage()
        emit(State.loading())

        //loading: SignIn process in progress
        val user = firebaseAuth.signInWithCredential(credential).await().user
        //Successfully registered scanner phone number
        emit(State.success(user!!))
    }.catch {
        //Failed to register
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * SignIn the user anonymously. This gives permission to a user to write to db and upload
     * files to storage without actually signIn.
     *
     */
    fun signInAnonymously() = flow{
        emit(State.loading())
        val anonymousUser = firebaseAuth.signInAnonymously().await()
        //Returns the user object
        emit(State.success(anonymousUser.user))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * Sign out the current user
     */
    fun signOutUser()= firebaseAuth.signOut()

}