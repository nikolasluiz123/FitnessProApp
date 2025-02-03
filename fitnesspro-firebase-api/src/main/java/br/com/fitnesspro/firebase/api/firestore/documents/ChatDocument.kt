package br.com.fitnesspro.firebase.api.firestore.documents

data class ChatDocument(
    val id: String = "",
    val receiverPersonName: String = "",
    val receiverPersonId: String = "",
    var lastMessage: String? = null,
    var lastMessageDate: Long? = null,
    var notReadMessagesCount: Int = 0
) {

    companion object {
        const val COLLECTION_NAME = "chats"
    }
}