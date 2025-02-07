package br.com.fitnesspro.firebase.api.firestore.documents

data class MessageNotificationDocument(
    val id: String = "",
    val text: String? = null,
    val date: Long? = null,
    val personReceiverId: String? = null,
    val personSenderName: String? = null,
) {

    companion object {
        const val COLLECTION_NAME = "messageNotifications"
    }
}