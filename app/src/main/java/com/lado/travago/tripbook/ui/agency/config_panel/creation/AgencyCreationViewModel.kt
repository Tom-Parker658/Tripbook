package com.lado.travago.tripbook.ui.agency.config_panel.creation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.admin.OnlineTravelAgency
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.StorageTags
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.regex.Pattern

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyCreationViewModel : ViewModel() {
    private val authRepo = FirebaseAuthRepo()
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()

    //LIVEDATA to display messages as toasts
    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    var firstRun = true

    //To either save or re-upload agency logo to FireStorage
    private val _onLogoSaved = MutableLiveData(false)
    val onLogoSaved: LiveData<Boolean> get() = _onLogoSaved

    private val _startSaving = MutableLiveData(false)
    val startSaving: LiveData<Boolean> get() = _startSaving

    //Live data to know when to navigate back when all info has been saved
    private val _onInfoSaved = MutableLiveData(false)
    val onInfoSaved: LiveData<Boolean> get() = _onInfoSaved

    //Loading state
    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    //Agency Fields
    var nameField = ""
        private set
    var mottoField = ""
        private set
    var creationDateInMillis = Date().time
        private set
    var decreeNumberField = ""
        private set
    var supportPhone1Field = ""
        private set
    var supportPhone2Field = ""
        private set
    var phoneCode1 = 237
        private set
    var phoneCode2 = 237
        private set
    var supportEmailField = ""
        private set

    //This logo urls can be empty if this is the first time we are creating the agency
    var logoUrl: String = ""
        private set

    //This stores the local path to any new image which has been used to change a preexisting one
    var logoUri: Uri? = null
        private set

    /**
     * This is either the existing document reference for the agency or a new empty one for a new agency
     * We initialise it with this empty document but can be changed if we get an existing agency document
     */
    private var agencyDocRef = firestoreRepo.db.collection("OnlineTransportAgency").document()

    enum class FieldTags {
        SUPPORT_PHONE_1,
        SUPPORT_PHONE_2,
        PHONE_CODE_1,
        PHONE_CODE_2,
        NAME,
        MOTTO,
        SUPPORT_EMAIL,
        LOGO_URI,
        DECREE_NUMBER,
        CREATION_DATE_IN_MILLIS,
        ON_LOGO_SAVED,
        TOAST_MESSAGE,
        START_SAVING,
        IS_FIRST_RUN,
        ON_LOADING,
        ON_INFO_SAVED
    }

    fun fillExistingData(agencyDoc: DocumentSnapshot) {
        firstRun = false
        agencyDocRef = agencyDoc.reference
        nameField = agencyDoc.getString("agencyName")!!
        decreeNumberField = agencyDoc.getString("creationDecree")!!
        creationDateInMillis = agencyDoc.getLong("creationDateInMillis")!!
        mottoField = agencyDoc.getString("motto")!!
        supportEmailField = agencyDoc.getString("supportEmail")!!
        logoUrl = agencyDoc.getString("logoUrl")!!
        phoneCode1 = agencyDoc.getLong("phoneCode1")!!.toInt()
        phoneCode2 = agencyDoc.getLong("phoneCode2")!!.toInt()
        supportPhone1Field = agencyDoc.getString("supportPhone1")!!
        supportPhone2Field = agencyDoc.getString("supportPhone2")!!
        _onLoading.value = false
    }

    /**
     * Saves the agency fields to a viewModel variables
     */
    fun setField(key: FieldTags, value: Any) {
        when (key) {
            FieldTags.NAME -> nameField = value.toString().trim()
            FieldTags.MOTTO -> mottoField = value.toString().trim()

            FieldTags.PHONE_CODE_1 -> phoneCode1 = value as Int
            FieldTags.PHONE_CODE_2 -> phoneCode2 = value as Int
            FieldTags.SUPPORT_PHONE_1 -> supportPhone1Field = value.toString().removeSpaces()
            FieldTags.SUPPORT_PHONE_2 -> supportPhone2Field = value.toString().removeSpaces()
            FieldTags.SUPPORT_EMAIL -> supportEmailField = value.toString().trim()
            FieldTags.DECREE_NUMBER -> decreeNumberField = value.toString().trim()
            FieldTags.CREATION_DATE_IN_MILLIS -> creationDateInMillis = value as Long
            FieldTags.LOGO_URI -> logoUri = value as Uri?
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOGO_SAVED -> _onLogoSaved.value = value as Boolean
            FieldTags.START_SAVING -> _startSaving.value = value as Boolean
            FieldTags.IS_FIRST_RUN -> firstRun = value as Boolean
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
            FieldTags.ON_INFO_SAVED -> _onInfoSaved.value = value as Boolean
        }
    }

    /**
     * Upload the logo to the storage
     */
    suspend fun saveLogo() {
        _onLoading.value = true
        storageRepo.uploadFile(
            logoUri!!,
            agencyDocRef.id,
            FirestoreTags.OnlineTransportAgency,
            StorageTags.LOGO
        ).collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    logoUrl = it.data

                    //Activate and deactivate
                    _onLogoSaved.value = true
                    _onLogoSaved.value = false

                    _onLoading.value = false
                }

            }
        }
    }

    /**
     * Saves a fresh copy of the agency info for a new agency
     */
    suspend fun createAgency(bookerDoc: DocumentSnapshot) = flow {
        emit(State.loading())
        val db = firestoreRepo.db
        val recordDocRef = db.collection("${agencyDocRef.path}/Record").document()
        val scannerDocRef =
            db.document("${agencyDocRef.path}/Scanners/${bookerDoc.id}")
        val newAgencyMap = OnlineTravelAgency(
            id = agencyDocRef.id,
            agencyName = nameField,
            logoUrl = logoUrl,
            motto = mottoField,
            creationDecree = decreeNumberField,
            supportEmail = supportEmailField,
            supportPhone1 = supportPhone1Field,
            supportPhone2 = supportPhone2Field,
            modifiedOn = null,
            creationDateInMillis = creationDateInMillis,
            supportCountryCode1 = phoneCode1,
            supportCountryCode2 = phoneCode2
        ).otaMap

        val creatorScannerMap = hashMapOf<String, Any?>(
            "id" to bookerDoc.id,
            "phone" to bookerDoc.getString("phone"),
            "photoUrl" to (bookerDoc.getString("photoUrl") ?: ""),
            "isAdmin" to true,
            "isOwner" to true,
            "active" to true,
            "scansNumber" to 0,
            "addedOn" to Timestamp.now(),
        )

        val changeMap = mapOf<String, Any>(
            "creatorId" to bookerDoc.id,
            "action" to "creation",
            "doneAt" to Timestamp.now()
        )

        firestoreRepo.db.runBatch { batch ->
            /**1- We Upload the new agency info into firestore*/

            batch.set(agencyDocRef, newAgencyMap) //TODO: REMOVE commit

            /**2- Adds the current user to the list of scanners with the admin tag and owner tag to true*/
            batch.set(scannerDocRef, creatorScannerMap)

            /**3- We make sure that the creator booker document contains knows that he is affiliated to an agency*/
            batch.update(
                bookerDoc.reference,
                "agencyID",
                agencyDocRef.id
            )
            /**4-We create a record doc*/
            batch.set(recordDocRef, changeMap)
        }.await()
        emit(State.success(Unit))
    }.apply {
        catch { emit(State.failed(it as Exception)) }
        flowOn(Dispatchers.IO)
        collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    _onLoading.value = false
                    _onInfoSaved.value = true
                    _onInfoSaved.value = false
                }

            }
        }
    }

    /**
     * Updates the agency to db
     */
    suspend fun updateAgencyInfo(bookerDoc: DocumentSnapshot) = flow {
        emit(State.loading())
        val agencyMapData = OnlineTravelAgency(
            id = bookerDoc.getString("agencyID")!!,
            agencyName = nameField,
            logoUrl = logoUrl,
            motto = mottoField,
            creationDecree = decreeNumberField,
            supportEmail = supportEmailField,
            supportPhone1 = supportPhone1Field,
            supportPhone2 = supportPhone2Field,
            creationDateInMillis = creationDateInMillis,
            supportCountryCode1 = phoneCode1,
            supportCountryCode2 = phoneCode2,
            modifiedOn = Timestamp.now()
        ).otaMap
        val db = firestoreRepo.db
        val agencyDocRef =
            db.document("OnlineTransportAgency/${bookerDoc.getString("agencyID")}")
        val recordDocRef = db.collection("${agencyDocRef.path}/Record").document()
        val changeMap = mapOf<String, Any>(
            "scannerId" to bookerDoc.id,
            "action" to "modification",
            "doneAt" to Timestamp.now()
        )

        firestoreRepo.db.runBatch { batch ->
            /**1- We Update agency info into firestore*/
            batch.update(agencyDocRef, agencyMapData)

            /**2- We add to records that this current admin scanner changed some details*/
            batch.set(recordDocRef, changeMap)
        }.await()
        emit(State.success(Unit))
    }.apply {
        catch { emit(State.failed(it as Exception)) }
        flowOn(Dispatchers.IO)
        collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    _onLoading.value = false
                    _onInfoSaved.value = true
                    _onInfoSaved.value = false
                }
            }
        }
    }
}