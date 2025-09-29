package br.com.fitnesspro.firebase.api.firestore.documents

import br.com.android.firebase.toolkit.firestore.document.FirestoreDocument

data class ChatDocument(
    val id: String = "",
    val receiverPersonName: String = "",
    val receiverPersonId: String = "",
    var lastMessage: String? = null,
    var lastMessageDate: Long? = null,
    var notReadMessagesCount: Int = 0
): FirestoreDocument() {

    companion object {
        const val COLLECTION_NAME = "chats"
    }
}