package com.lado.travago.tripbook.repo.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.lado.travago.tripbook.repo.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
     * @return FirebaseUser which the signIn scanner(booker, scanner etc) see [FirebaseUser]
     */
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) = flow {
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
     * A sign-in method which uses either a phone number or an credential to SignIn the user
     */
    fun logInUserWithEmail(email: String, password: String) = flow{
        firebaseAuth.useAppLanguage()

        emit(State.loading())

        val user = firebaseAuth.signInWithEmailAndPassword(email, password).await().user
        emit(State.success(user!!))
    }.catch{
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * A -in method which uses either a phone number or an credential to SignIn the user
     */
    fun createUserWithEmail(email: String, password: String) = flow{
        firebaseAuth.useAppLanguage()

        emit(State.loading())

        val user = firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
        emit(State.success(user!!))
    }.catch{
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * A method to send the emailLink to the user credential address
     *
    fun sendSignInLinkToUserEmail(credential: String, packageName: String) =  flow{
        emit(State.loading())

        val actionCodeSettings = actionCodeSettings {
            url = "https://tripbook.auth.com/finishSignup/"
            handleCodeInApp = true
            setAndroidPackageName(
                packageName,
                true,
                null
            )
        }
        val x = firebaseAuth.sendSignInLinkToEmail(credential, actionCodeSettings).await()
        emit(State.success(x))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
*/
    /**
     * SignIn the user anonymously. This gives permission to a user to write to db and upload
     * files to storage without actually signIn.
     *
     */
    fun signInAnonymously() = flow {
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
    fun signOutUser() = firebaseAuth.signOut()


    /**
     * Delete the current anonymous user from firestore
     */
    fun deleteCurrentUser() = flow {
        emit(State.loading())
        val deleteTask = firebaseAuth.currentUser!!.delete().await()
        emit(State.success(deleteTask))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}



