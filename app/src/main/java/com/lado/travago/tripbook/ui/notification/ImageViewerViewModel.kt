package com.lado.travago.tripbook.ui.notification

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi

import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.model.enums.PlaceHolder
import com.lado.travago.tripbook.repo.firebase.StorageRepo


@ExperimentalCoroutinesApi
class ImageViewerViewModel(
    var imageUrl: String?,
    var imageUri: Uri?,
    var currentImageBitmap: Bitmap?,
    var placeholder: PlaceHolder,
    val isEditable: Boolean,
    val isDeletable: Boolean,
    val doCompression: Boolean,
) : ViewModel() {
    val firestoreRepo = FirestoreRepo()
    val storageRepo = StorageRepo()

    private val _reloadImage = MutableLiveData(true)
    val reloadImage: LiveData<Boolean> get() = _reloadImage

    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    enum class FieldTags { ON_LOADING, TOAST_MESSAGE, DO_RELOAD_IMAGE, URL, URI }

    fun setField(fieldTag: FieldTags, value: Any?) =
        when (fieldTag) {
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
//            FieldTags.BITMAP_IMAGE -> currentImageBitmap = value as Bitmap?
            FieldTags.DO_RELOAD_IMAGE -> _reloadImage.value = value as Boolean
            FieldTags.URL -> imageUrl = value as String?
            FieldTags.URI -> imageUri = value as Uri?
        }

    companion object {
        class ImageViewerViewModelFactory(
            private val imageUrl: String?,
            private val imageUri: Uri?,
            private val currentImageBitmap: Bitmap?,
            private val placeHolder: PlaceHolder,
            private val isEditable: Boolean,
            private var isDeletable: Boolean,
            private var doCompression: Boolean,
        ) : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ImageViewerViewModel(
                    imageUrl,
                    imageUri,
                    currentImageBitmap,
                    placeHolder,
                    isEditable,
                    isDeletable,
                    doCompression
                ) as T
            }
        }
    }
}

