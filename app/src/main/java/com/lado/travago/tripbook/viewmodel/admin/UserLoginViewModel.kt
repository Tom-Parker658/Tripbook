package com.lado.travago.tripbook.viewmodel.admin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

/**
 * A view model to manage all login operations
 */
@ExperimentalCoroutinesApi
class UserLoginViewModel : ViewModel() {
    private val authRepo = FirebaseAuthRepo()
    var email = ""
        private set
    var password = ""
        private set

    private var _loading = MutableLiveData(false)
    val loading get() = _loading

    //Becomes through when the login is successful
    private var _userLoggedIn = MutableLiveData(false)
    val userLoggedIn get() = _userLoggedIn

    enum class LoginFieldTags { EMAIL, PASSWORD }

    /**
     * Sets the Fields
     */
    fun setField(field: String, tag: LoginFieldTags) =
        when (tag) {
            LoginFieldTags.EMAIL -> email = field
            LoginFieldTags.PASSWORD -> password = field
        }

    suspend fun loginUser() =
        authRepo.logInUserWithEmail(email, password).collect {
            when (it) {
                is State.Loading -> _loading.value = true
                is State.Failed -> {
                    Log.e("UserLogin", "Failed: ${it.message}")
                    _loading.value = false
                }
                //Navigate back to the caller activity
                is State.Success -> {
                    Log.i("UserLogin", "Login Success ${it.data.uid}")
                    _userLoggedIn.value = true
                }
            }
        }

}