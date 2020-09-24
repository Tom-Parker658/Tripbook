package com.lado.travago.transpido.repo.firebase

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lado.travago.transpido.repo.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
class FirestoreRepo {
    //Instance of our firestore db
    private val db = FirebaseFirestore.getInstance()

    /**
     * Uses the .add() to add a document to a collection from the path. A random id is given to the
     * document using this method
     * @param data is the hashMap of String to Any to be stored in the document
     * @param collectionPath is the path of the collection where the document will be added to
     * @return a flow state containing the path to the document in success STATE
     */
    fun addDocument(
        data: HashMap<String, Any?>,
        collectionPath: String,
    ) = flow {
        emit(State.loading())

        val collection = db.collection(collectionPath)
        val document = collection.add(data).await()
        emit(State.success(document.path))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * Uses .set() to set data to a document from the documentPath. The last segment of the path is
     * turned as the id for the document
     * @param data is the hashMap of String to Any to be stored in the document
     * @param documentPath is the reference to the document where the data will be set to
     * @return a Null flow state
     */
    fun setDocument(
        data: HashMap<String, Any?>,
        documentPath: String,
    )= flow{
        emit(State.loading())

        val document = db.document(documentPath)
        document.set(data)
        emit(State.success(null))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * Increments a number in the database and returns the new value of the field
     * @param byValue the number to be added to the number
     * @param documentPath is the path to the document
     * @param fieldName is the field we want to increment
     * @return a flow state containing the new value to the document in success STATE
     */
    fun incrementField(
        byValue: Number,
        documentPath: String,
        fieldName: String
    ) = flow{
        emit(State.loading())

        val document = db.document(documentPath)
        document.update(fieldName, FieldValue.increment(byValue.toDouble())).await()
        val newFieldValue = document.get().await()[fieldName] as Number
        emit(State.success(newFieldValue))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

}