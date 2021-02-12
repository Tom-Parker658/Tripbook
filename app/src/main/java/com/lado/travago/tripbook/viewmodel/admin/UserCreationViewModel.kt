package com.lado.travago.tripbook.viewmodel.admin

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.model.enums.OCCUPATION
import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.model.users.Booker
import com.lado.travago.tripbook.model.users.Scanner
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.StorageTags
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class UserCreationViewModel(val agencyName: String?, private val agencyPath: String?) : ViewModel() {
    //Precise which type of user we are dealing with. if the agencyId and agencyName are not null, we know it is a
    //Scanner else a Booker
    private var _isUserAScanner  = MutableLiveData(agencyName != null)
    val isUserAScanner get() = _isUserAScanner

    //FirebaseRepo utilities for db, signIn and cloud storage
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()
    private val authRepo = FirebaseAuthRepo()


    //LiveData to know when a process is in loading state
    private val _loading = MutableLiveData(false)
    val loading get() = _loading
    

    //LiveData to know when the creation of the User is completed
    private val _userCreated = MutableLiveData(false)
    val userCreated get() = _userCreated


    //liveData which holds the phone number of
    private lateinit var generatedID: String

    //Values of the fields from the creationForm
    var nameField = ""
        private set
    var birthdayField = 0L
        private set
    var email = ""
        private set
    var birthplaceField = ""
        private set
    var isAdminField = false
        private set
    var passwordField = ""
        private set
    var photoField: Bitmap? = null
        private set
    var sex = SEX.UNKNOWN
        private set
    var sexFieldId = 0
        private set
    var photoUrl = ""
        private set
    var occupationField = OCCUPATION.UNKNOWN
        private set


    /**
     * A function to set the value the fields from the creation form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setFields(fieldTag: FieldTags, value: Any) = when(fieldTag){
        FieldTags.NAME -> nameField = value.toString()
        FieldTags.BIRTHDAY -> birthdayField = value as Long
        FieldTags.EMAIL -> email = "$value"
        FieldTags.SEX_ID -> sexFieldId = value as Int
        FieldTags.IS_ADMIN -> isAdminField = value as Boolean
        FieldTags.PROFILE_PHOTO -> photoField = value as Bitmap?
        FieldTags.BIRTH_PLACE -> birthplaceField = value.toString()
        FieldTags.SEX -> sex = value as SEX
        FieldTags.ID -> generatedID = value as String
        FieldTags.PASSWORD -> passwordField = value.toString()
        FieldTags.OCCUPATION -> occupationField = OCCUPATION.STUDENT
    }


    /**
     * Contains different identifiers for the fields in our creation form
     */
    enum class FieldTags {
        NAME, SEX_ID, SEX, IS_ADMIN, EMAIL, BIRTHDAY, BIRTH_PLACE, PROFILE_PHOTO, ID, OCCUPATION, PASSWORD
    }


    /**
     * Adds a [Scanner] or [Booker] instead to the database if the values of [agencyPath] and [agencyName] are all null
     */
    suspend fun createUser(){
        /**
         * Uses [FirebaseAuthRepo.logInUserWithEmail] to sign in scanner to firestore and generate an id
         */
        _loading.value = true
        authRepo.createUserWithEmail(email, passwordField).collect{ authState ->
            when(authState) {
                is State.Success -> {
                    Log.i("PhoneAuth", "FireAuth OK! ID =${authState.data}")
                    //Get id from created user
                    generatedID = authState.data.uid

                    val photoStream =
                        Utils.convertBitmapToStream(photoField!!, Bitmap.CompressFormat.PNG, 0)

                    //Upload the profile photo to the cloud storage and retrieve url
                    storageRepo.uploadPhoto(
                        photoStream,
                        generatedID,
                        FirestoreTags.Users,
                        StorageTags.PROFILE
                    ).collect { storageState ->
                        when (storageState) {
                            is State.Loading -> _loading.value = true
                            is State.Failed -> {
                                loading.value = false
                                Log.e(TAG, storageState.message)
                            }
                            is State.Success -> {
                                Log.i("PhoneAuth", "FireStorage OK! link =${storageState.data}")
                                photoUrl = storageState.data
                                //The scanner map as a booker
                                val booker = Booker(
                                    uid = generatedID,
                                    name = nameField,
                                    sex = sex,
                                    birthdayInMillis = birthdayField,
                                    photoUrl = photoUrl,
                                    birthPlace = birthplaceField,
                                    occupation = occupationField,
                                    phoneNumber = email
                                )
                                //Complete the creation process and logout
                                //Adds the user as a Booker
                                firestoreRepo.setDocument(
                                    booker.userMap,
                                    "${FirestoreTags.Bookers}/${generatedID}"
                                ).collect {
                                    when (it) {
                                        is State.Success -> {
                                            Log.i("PhoneAuth", "FireStore OK! Booker done" )
                                            //Adds the user as a scanner to the database under his agency
                                            if (isUserAScanner.value!!) {
                                                Log.i("PhoneAuth", "Booker is a scanner!" )

                                                //The scanner map
                                                val scanner = Scanner(
                                                    uid = generatedID,
                                                    name = nameField,
                                                    birthdayInMillis = birthdayField,
                                                    sex = sex,
                                                    photoUrl = photoUrl,
                                                    phoneNumber = email,
                                                    otaName = agencyName?:"",
                                                    isAdmin = isAdminField,
                                                    birthPlace = birthplaceField
                                                )
                                                firestoreRepo.setDocument(
                                                    scanner.userMap,
                                                    "$agencyPath/${FirestoreTags.Scanners}/$generatedID"
                                                ).collect { dbState ->
                                                    when (dbState) {
                                                        is State.Failed -> {
                                                            authRepo.signOutUser()
                                                            _loading.value = false
                                                            Log.e("UserCreation", dbState.message)
                                                        }
                                                        is State.Success -> {
                                                            authRepo.signOutUser()
                                                            _loading.value = false
                                                            _userCreated.value = true
                                                        }
                                                        is State.Loading -> {
                                                            authRepo.signOutUser()
                                                            _loading.value = true
                                                        }
                                                    }
                                                }
                                            } else {
                                                authRepo.signOutUser()
                                                _loading.value = false
                                                _userCreated.value = true
                                            }
                                        }
                                        is State.Loading -> _loading.value = true
                                        is State.Failed -> {
                                            authRepo.signOutUser()
                                            _loading.value = false
                                            Log.e(TAG, it.message)
                                        }
                                    }
                                }

                            }
                        }

                    }
                }
                is State.Failed -> {
                    Log.e("PhoneAuth", authState.message)
                }
                is State.Loading -> {
                    Log.i("PhoneAuth", "Creating User")
                }
            }
        }

    }

    /**
     * Adds a [Booker] instead to the database if the values of [agencyPath] and [agencyName] are all null
     */

    fun startLoading(){_loading.value = true}
    fun stopLoading(){_loading.value = false}

    fun formatDate(timeInMillis: Long) = Utils.formatDate(timeInMillis, "MMMM, dd YYYY")

    companion object{
        const val TAG = "UserCreationVM"
    }
}



