package br.com.fitnesspro.firebase.api.firestore.documents

data class PersonDocument(
    val id: String? = null,
    val name: String? = null
): FirestoreDocument() {

    companion object {
        const val COLLECTION_NAME = "persons"
    }
}