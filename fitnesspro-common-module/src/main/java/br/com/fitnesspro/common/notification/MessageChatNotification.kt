package br.com.fitnesspro.common.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.notification.FitnessProNotification
import br.com.fitnesspro.firebase.api.firestore.documents.MessageNotificationDocument

class MessageChatNotification(
    context: Context,
    private val messages: List<MessageNotificationDocument>
): FitnessProNotification(context) {

    override fun getChannelId(): String = NEW_MESSAGE_CHAT_CHANNEL_ID

    override fun getChannelName(): String = context.getString(R.string.new_message_chat_channel_name)

    override fun getChannelDescription(): String = context.getString(R.string.new_message_chat_channel_description)

    override fun getNotificationId(): Int = NEW_MESSAGE_CHAT_NOTIFICATION_ID

    override fun getImportance(): Int = NotificationManager.IMPORTANCE_HIGH

    override fun onBuildNotification(builder: NotificationCompat.Builder) {
        super.onBuildNotification(builder)

        val user = Person.Builder().setName(messages.first().personSenderName).build()
        val messagingStyle = NotificationCompat.MessagingStyle(user)

        messages.forEach { message ->
            messagingStyle.addMessage(message.text, message.date!!, user)
        }

        builder.setStyle(messagingStyle)
    }

    companion object {
        const val NEW_MESSAGE_CHAT_NOTIFICATION_ID = 1
        const val NEW_MESSAGE_CHAT_CHANNEL_ID = "new_message_chat_channel_id"
    }

}