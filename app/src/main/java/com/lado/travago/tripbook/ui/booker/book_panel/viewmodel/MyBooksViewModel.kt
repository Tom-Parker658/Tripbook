package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class MyBooksViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()
    val authRepo = FirebaseAuthRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _retry = MutableLiveData(true)
    val retry: LiveData<Boolean> get() = _retry


    // The list of all books for the current booker
    private val _allMyBooks = MutableLiveData(listOf<DocumentSnapshot>())
    val allMyBooks: LiveData<List<DocumentSnapshot>> get() = _allMyBooks

    // This is a temporary holder to store all sorted, search results books
    private val _sortResultBookList = MutableLiveData(mutableListOf<DocumentSnapshot>())
    val sortResultBookList: LiveData<MutableList<DocumentSnapshot>> get() = _sortResultBookList

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    enum class FieldTags { ON_LOADING, TOAST_MESSAGE }

    /**
     * This method will get all books from all days
     */
    suspend fun getAllBooks() {
        firestoreRepo.getCollection(
            "Bookers/${authRepo.firebaseAuth.currentUser!!.uid}/Books", Source.DEFAULT,
        ).collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    _allMyBooks.value = it.data.documents
                    _onLoading.value = false
                }
            }
        }
    }

    /**
     * Sorts the cache data
     */
    fun sortBooks(fieldPath: String) {
        //We will try to sort the already sorted list to get more precise results if possible else we sort original List
        val tempToBeSorted =
            if (_sortResultBookList.value!!.isNotEmpty()) _sortResultBookList.value!!
            else allMyBooks.value!!

        _sortResultBookList.value = tempToBeSorted.sortedBy {
            it[fieldPath] as Comparable<Any>
        }.toMutableList()
    }

    /**
     * Just query data already found in the cache
     */
    suspend fun searchBooks(query: (collection: CollectionReference) -> Query) {
        firestoreRepo.queryCollection(
            "Bookers/${authRepo.firebaseAuth.currentUser!!.uid}/Books", Source.CACHE, query
        ).collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    _onLoading.value = false
                    _sortResultBookList.value = it.data.documents
                }
            }
        }
    }

    fun setField(fieldTag: FieldTags, value: Any) =
        when (fieldTag) {
            FieldTags.ON_LOADING -> _toastMessage.value = value.toString()
            FieldTags.TOAST_MESSAGE -> _onLoading.value = value as Boolean
        }

    fun clearFilters() {
        _sortResultBookList.value!!.clear()
    }
}
