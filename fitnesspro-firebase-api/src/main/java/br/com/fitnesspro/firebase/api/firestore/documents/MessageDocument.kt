package br.com.fitnesspro.firebase.api.firestore.documents

import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState
import java.util.UUID

data class MessageDocument(
    val id: String = UUID.randomUUID().toString(),
    val text: String? = null,
    val personSenderId: String? = null,
    var date: Long? = null,
    var state: String = EnumMessageState.SENDING.name,
): FirestoreDocument() {

    companion object {
        const val COLLECTION_NAME = "messages"
    }
}