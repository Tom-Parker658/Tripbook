package com.lado.travago.tripbook.model.admin

/**
 * This is model of the normal firestore document with a hashmap and a path.
 * @param path is the firestore path
 */
data class OfflineDocument(
    val path: String
) {
    private val _dataMap = mutableMapOf<String, Any?>()
    val dataMap get() = (_dataMap to HashMap<String, Any?>())

    /**
     * adds or modifies a specific field to the data map
     */
    fun add(field: String, value: Any?) {
        _dataMap[field] = value
    }

    /**
     * adds many fields to the map
     */
    fun map(dataMap: HashMap<String, Any?>){
        _dataMap.putAll(dataMap)
    }


    /**
     * deletes a field from the data map
     */
    fun del(field: String) {
        _dataMap.remove(field)
    }


}

/**
 * This is a collection just like that in firestore but offline
 * @param name the collection path
 */
data class OfflineCollection(
    val name: String
){
    private val _documents = mutableMapOf<String, OfflineDocument>()
    val documents get() = _documents to HashMap<String, OfflineDocument>()

    /**
     * gets a specific document from the offline collection or creates one if not found
     */
    fun document(path: String) = _documents[path]

    /**
     * Deletes a particular document
     */
    fun del(path: String) = _documents.remove(path)
}

