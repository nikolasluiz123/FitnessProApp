package br.com.fitnesspro.firebase.api.firestore.documents

import br.com.android.firebase.toolkit.firestore.document.FirestoreDocument

data class PersonDocument(
    val id: String? = null,
    val name: String? = null
): FirestoreDocument() {

    companion object {
        const val COLLECTION_NAME = "persons"
    }
}