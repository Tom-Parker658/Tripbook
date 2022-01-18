package com.lado.travago.tripbook.ui.agency.config_panel.viewmodel

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.UIUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * A place where admin scanners can configure the agency
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyConfigViewModel : ViewModel() {
    private var firestoreRepo: FirestoreRepo = FirestoreRepo()
    var authRepo: FirebaseAuthRepo = FirebaseAuthRepo()

    var isFirstLaunch = true

    private val _bookerDoc = MutableLiveData<DocumentSnapshot>()
    val bookerDoc: LiveData<DocumentSnapshot> get() = _bookerDoc

    private val _onLoading = MutableLiveData<Boolean>()
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _scannerDoc = MutableLiveData<DocumentSnapshot>()
    val scannerDoc: LiveData<DocumentSnapshot> get() = _scannerDoc

    //Dictates when we we can use the retry button
    private val _retryable = MutableLiveData<Boolean>()
    val retryable: LiveData<Boolean?> get() = _retryable

    //Know if booker is a scanner or affiliated to any agency
    private val _bookerIsNotScanner = MutableLiveData<Boolean>()
    val bookerIsNotScanner: LiveData<Boolean> get() = _bookerIsNotScanner

    //true when the booker has not signedIn(i.e There is no current firebase account on the device)
    private val _noCachedData = MutableLiveData<Boolean>()
    val noCachedData: LiveData<Boolean> get() = _noCachedData

    //True if the current booker has a profile
    private val _hasProfile = MutableLiveData<Boolean>()
    val hasProfile: LiveData<Boolean> get() = _hasProfile

    //Holds the path to navigate to
    private val _navArgs = MutableLiveData(Bundle.EMPTY)
    val navArgs: LiveData<Bundle> get() = _navArgs

    lateinit var bookerListener: ListenerRegistration
    lateinit var scannerListener: ListenerRegistration


    /**
     * @since 15-01-2022
     *
     * Tries to get the scanner document associated to that booker if he is a scanner of course (>_<)
     * Notice that when ever we fail because of a network error, we mark [_noCachedData] true
     */
    private fun scannerDoc(agencyID: String, context: Context) {
//        Log.d("GATEWAY", "ScannerDoc called")
        scannerListener =
            firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Scanners/${authRepo.currentUser!!.uid}")
                .addSnapshotListener { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                    _onLoading.value = false
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.UNAVAILABLE) _noCachedData.value =
                            true
                        _toastMessage.value =
                            context.getString(R.string.text_unexpected_error_retry)
                        _retryable.value = true
                    }

                    if (value != null && value.exists()) {
                        _scannerDoc.value = value
                    } else {
                        _toastMessage.value =
                            context.getString(R.string.text_unexpected_error_retry)
                        _retryable.value = true
                        _bookerIsNotScanner.value = true
                    }

                }

    }

    /**
     * @since 15-01-2022
     *
     * Tries to get the current user booker document(at least when we know the booker is logged In)
     */
    fun bookerDoc(context: Context, uiUtils: UIUtils) {
        _onLoading.value = true
        _retryable.value = false
        bookerListener =
            firestoreRepo.db.document("Bookers/${authRepo.currentUser!!.uid}")
                .addSnapshotListener { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                    if (value != null) {
                        val agencyID = value.getString("agencyID")
                        when {
                            !agencyID.isNullOrBlank() -> scannerDoc(agencyID, context)
                            uiUtils.isConnected.value == false -> {
                                _noCachedData.value = true
                                _retryable.value = true
                                Log.d("GATEWAY", "Call2")
                            }
                            else -> {
                                Log.d("GATEWAY", "Call1")
                                _bookerIsNotScanner.value = true
                                _retryable.value = true
                            }
                        }
                    }
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.UNAVAILABLE) _noCachedData.value =
                            true
                        _toastMessage.value =
                            context.getString(R.string.text_unexpected_error_retry)
//                        _retryable.value = true
                    }

                }
    }

    /**
     * @since 17-01-2022
     *
     * #DONTREDO i.e We don't want to update the a variable with the same value it already stored
     */
    fun setField(tag: FieldTags, value: Any) {
        when (tag) {
            FieldTags.NO_CACHED_DATA -> {
                if (_noCachedData.value != value)
                    _noCachedData.value = value as Boolean
            }
            FieldTags.HAS_PROFILE -> {
                if (_hasProfile.value != value)
                    _hasProfile.value = value as Boolean
            }
            FieldTags.NAV_ARGS -> {
                if (_navArgs != value)
                    _navArgs.value = value as Bundle
            }
            FieldTags.BOOKER_IS_NOT_SCANNER -> {
                if (value != _bookerIsNotScanner.value)
                    _bookerIsNotScanner.value = value as Boolean
            }
            FieldTags.RETRYABLE -> {
                if (_retryable.value != value)
                    _retryable.value = value as Boolean?
            }
            FieldTags.TOAST_MESSAGE ->

                _toastMessage.value = value as String
        }
    }

    enum class FieldTags {
        NO_CACHED_DATA, HAS_PROFILE, NAV_ARGS, BOOKER_IS_NOT_SCANNER, RETRYABLE, TOAST_MESSAGE
    }

}
