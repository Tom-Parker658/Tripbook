package com.lado.travago.tripbook.repo.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.lado.travago.tripbook.model.enums.DbOperations
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
class FirestoreRepo(context: Context? = null) {
    //Instance of our firestore db
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance(FirebaseApp.getInstance())

    /**
     * A little prototype class to organise data to enter a batched operation
     */
    data class BatchedWritesInfo(
        val documentPath: String,
        val data: HashMap<String, Any?>,
        val dbOperation: DbOperations
    )

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
        document.set(data).await()
        emit(State.success(null))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun batchedWriteDocuments(
        batchedWritesInfoList: List<BatchedWritesInfo>
    ) = flow{
        emit(State.loading())
        db.runBatch {batchWriter ->
            for (info in batchedWritesInfoList){
                when(info.dbOperation){
                    DbOperations.SET -> {
                        batchWriter.set(
                            db.document(info.documentPath),
                            info.data
                        )
                    }
                    DbOperations.DELETE -> {
                       // TODO("What to do when we instead need to delete a field using batched write")
                    }
                    DbOperations.UPDATE -> {
                        //TODO("What to do when we instead need to update a field sing batched write")
                    }

                }
            }
        }.await()
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

    /**
     * This function is used to get a specific document using its path
     * @param docPath is the firestore document path
     * @param from is the [Source] from which we want to get the data e.g from server [Source.SERVER],
     * from cache [Source.CACHE] or start from cache and if not found, look from server [Source.DEFAULT]
     * @return the document snapshot which contains all info about the document
     */
    fun getDocument(docPath: String, from: Source = Source.DEFAULT) = flow {
        emit(State.loading())
        val doc = db.document(docPath)
        val documentSnapshot = doc.get(from).await()
        emit(State.success(documentSnapshot))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * Query a collection and returns documents which meets the provided requirement
     * @param collectionPath is the firestore path of the collection you which to query
     * @param from is the [Source] from which we want to get the data
     * @param query is a lambda containing the required specifications(Conditions) which we use as the [Query].
     *  This does the query on the required collection reference returned from the [collectionPath]
     * @return a [QuerySnapshot] which contains all documents which matched the query
     */
    fun queryCollection(
        collectionPath: String,
        from: Source = Source.DEFAULT,
        query: (collection: CollectionReference) -> Query
    ) =
        flow {
            emit(State.loading())
            val collectionRef = db.collection(collectionPath)
            val documents = query(collectionRef)
                .get(from)
                .await()
            emit(State.success(documents))
        }

    /**
     * Gets all the documents from a collection
     */
    fun getAllDocuments(collectionPath: String, from: Source = Source.DEFAULT) = flow {
        emit(State.loading())
        val collectionRef = db.collection(collectionPath)
        val allDocuments = collectionRef.get(from).await()
        emit(State.success(allDocuments))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * Identifies the current user. If it is a "Booker" or "Scanner"
     * @param uid uses the uid to query the document and then checks the origin of the uid either from
     * Scanner collection of Booker Booker collection
     */
    fun identifyUser(uid: String) = flow {
        emit(State.loading())
        val doc = db.document("${FirestoreTags.Scanners}/$uid").get().await()
        emit(State.success(doc))
    }.catch {
        emit(State.failed(FirestoreTags.Bookers.toString()))
    }.flowOn(Dispatchers.IO)
}