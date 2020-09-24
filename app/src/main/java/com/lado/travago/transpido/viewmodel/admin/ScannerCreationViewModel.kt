package com.lado.travago.transpido.viewmodel.admin

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.transpido.model.admin.OnlineTravelAgency
import com.lado.travago.transpido.model.admin.Scanner
import com.lado.travago.transpido.model.enums.SEX
import com.lado.travago.transpido.repo.FirestoreTags
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.StorageTags
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import com.lado.travago.transpido.repo.firebase.StorageRepo
import com.lado.travago.transpido.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ScannerCreationViewModel(private val agencyFirestorePath: String, val agencyName: String) : ViewModel() {

    //FirebaseRepo utilities for db, signIn and cloud storage
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()

    //LiveData to know when a process is in loading state
    private val _loading = MutableLiveData(false)
    val loading get() = _loading
    //LiveData to know when the creation of the scanner is completed
    private val _scannerCreated = MutableLiveData(false)
    val scannerCreated get() = _scannerCreated
    //liveData which holds the phone number of
    private lateinit var generatedID: String

    //Values of the fields from the creationForm
    var nameField = ""
        private set
    var birthdayField = 0L
        private set
    var phoneField = ""
        private set
    var birthplaceField = ""
        private set
    var isAdminField = false
        private set
    var photoField: Bitmap? = null
        private set
    var sex = SEX.UNKNOWN
        private set
    var sexFieldId = 0
        private set
    var url = ""
        private set
    /**
     * A function to set the value the fields from the creation form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setFields(fieldTag: FieldTags, value: Any) = when(fieldTag){
        FieldTags.NAME -> this.nameField = value.toString()
        FieldTags.BIRTHDAY -> birthdayField = value as Long
        FieldTags.PHONE -> phoneField = value.toString()
        FieldTags.SEX_ID -> sexFieldId = value as Int
        FieldTags.IS_ADMIN -> isAdminField = value as Boolean
        FieldTags.PROFILE_PHOTO -> photoField = value as Bitmap
        FieldTags.BIRTH_PLACE -> birthplaceField = value.toString()
        FieldTags.SEX -> sex = value as SEX
        FieldTags.ID -> generatedID = value as String
    }


    /**
     * Contains different identifiers for the fields in our creation form
     */
    enum class FieldTags {
        NAME, SEX_ID, SEX, IS_ADMIN, PHONE, BIRTHDAY, BIRTH_PLACE, PROFILE_PHOTO, ID
    }

    /**
     * Adds the scanner to the db, upload the photo
     */
    suspend fun createScanner(){
        val photoStream = Utils.convertBitmapToStream(photoField!!, Bitmap.CompressFormat.PNG, 0)
        //Upload the profile photo to the cloud storage and retrieve url
        storageRepo.uploadPhoto(
            photoStream,
            generatedID,
            FirestoreTags.Scanner,
            StorageTags.PROFILE
        ).collect{storageState ->
            when(storageState){
                is State.Loading -> _loading.value = true
                is State.Failed -> {
                    loading.value = false
                    Log.e(TAG, storageState.message)
                }
                is State.Success -> {
                    url = storageState.data
                    val scanner = Scanner(
                        generatedID,
                        nameField,
                        birthdayField,
                        sex,
                        url,
                        phoneField,
                        agencyName,
                        null,
                        birthplaceField,
                        "Cameroon",
                        isAdminField
                    )

                    //Adds the scanner to the database
                    firestoreRepo.setDocument(
                        scanner.scannerMap,
                        "${FirestoreTags.Scanner}/${scanner.scannerMap["uid"]}"
                    ).collect {dbState ->
                        when(dbState){
                            is State.Loading -> _loading.value = true
                            is State.Failed -> {
                                _loading.value = false
                                Log.e(TAG, dbState.message)
                            }
                            is State.Success -> {
                                _loading.value = false
                                val otaScannerMap = OnlineTravelAgency.otaScannerMap(scanner)
                                //e.g OTA/General/Scanners/Administrator/scanner/Tom Joe
                                val otaScannerPath =
                                    "$agencyFirestorePath/Scanners${
                                        if(scanner.isAdmin) "Administrators" else "Standard"} ${scanner.name}"

                                //Add some data about the scanner to the document of the agency
                                firestoreRepo.setDocument(
                                    otaScannerMap,
                                    otaScannerPath
                                ).collect {dbState1 ->
                                    when(dbState1){
                                        is State.Loading -> _loading.value = true
                                        is State.Failed -> {
                                            _loading.value = false
                                            Log.e(TAG, dbState1.message)
                                        }
                                        is State.Success -> {
                                            _loading.value = false
                                            //Complete the creation process
                                            _scannerCreated.value = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    fun formatDate(timeInMillis: Long) =
        Utils.formatDate(timeInMillis, "MMMM, dd YYYY")

    companion object{
        const val TAG = "ScannerCreationVM"
    }
}


