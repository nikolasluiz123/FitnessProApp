package br.com.fitnesspro.common.services

import android.annotation.SuppressLint
import br.com.fitnesspro.common.notification.GenericCommunicationNotification
import br.com.fitnesspro.common.notification.SchedulerNotification
import br.com.fitnesspro.shared.communication.enums.notification.EnumNotificationChannel
import br.com.fitnesspro.shared.communication.notification.FitnessProNotificationData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FitnessProFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val channelValue = remoteMessage.data[FitnessProNotificationData::channel.name]!!
        val channel = EnumNotificationChannel.valueOf(channelValue)

        when (channel) {
            EnumNotificationChannel.NEW_MESSAGE_CHAT_CHANNEL -> {
//                MessageChatNotification(baseContext)
            }

            EnumNotificationChannel.SCHEDULER_CHANNEL -> {
                SchedulerNotification(baseContext).showNotification(
                    title = remoteMessage.data[FitnessProNotificationData::title.name]!!,
                    message = remoteMessage.data[FitnessProNotificationData::message.name]!!
                )
            }

            EnumNotificationChannel.GENERIC_COMMUNICATION_CHANNEL -> {
                GenericCommunicationNotification(baseContext).showNotification(
                    title = remoteMessage.data[FitnessProNotificationData::title.name]!!,
                    message = remoteMessage.data[FitnessProNotificationData::message.name]!!
                )
            }
        }
    }
}