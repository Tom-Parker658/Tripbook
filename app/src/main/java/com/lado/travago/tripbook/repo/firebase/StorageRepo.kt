package com.lado.travago.tripbook.repo.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.StorageTags
import com.lado.travago.tripbook.utils.AdminUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.io.InputStream

@ExperimentalCoroutinesApi
class StorageRepo {
    var storage = FirebaseStorage.getInstance()

    init {
        storage.useEmulator(
            AdminUtils.LOCAL_SERVER_FIREBASE_IP,
            9199
        )
    }

    /**
     * Adds a photo to FireStorage asynchronously as a .jpg file and return url
     * @param stream is the image converted into stream for faster upload
     * @param filename is the name or identifier of the uploaded image e.g uid, name etc
     * @param userType is the name of the type of user of tranSpeed to which the image is attributed
     * to e.g TRAVEL_AGENCY. see [FirestoreTags]
     * @param imageType is the name of the type of image e.g PROFILE see [StorageTags]
     * @return the url of the image as a String
     */
    fun uploadStream(
        stream: InputStream,
        filename: String,
        userType: FirestoreTags,
        imageType: StorageTags,
    ) = flow {
        emit(State.loading())
        //Loading
        val storageRef = storage.reference
        val imagesRef =
            storageRef.child("images/${imageType.name}/${userType.name}/${filename}.jpg")
        val photoUrl = imagesRef.putStream(stream).await()
            .storage.downloadUrl.await().toString()

        //Process complete
        emit(State.success(photoUrl))
    }.catch {
        //Process failed
        emit(State.failed(it as Exception))
    }.flowOn(Dispatchers.IO)

    fun uploadFile(
        uri: Uri,
        agencyID: String,
        userType: FirestoreTags,
        imageType: StorageTags,
    ) = flow {
        emit(State.loading())
        //Loading
        val storageRef = storage.reference
        val imagesRef =
            storageRef.child("images/${imageType.name}/${userType.name}/$agencyID")
        val photoUrl = imagesRef.putFile(uri).await()
            .storage.downloadUrl.await().toString()

        //Process complete
        emit(State.success(photoUrl))
    }.catch {
        //Process failed
        emit(State.failed(it as Exception))
    }.flowOn(Dispatchers.IO)

    /**
     * It retrieves a downloadable url of the image
     * @param path A firestore-like path
     */
    fun url(
        path: String,
    ) = flow {
        emit(State.loading())

        val url = storage.getReference(path).downloadUrl.await()
        emit(State.success(url))
    }.catch {
        emit(State.failed(it as Exception))
    }.flowOn(Dispatchers.IO)
}