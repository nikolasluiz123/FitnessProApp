package br.com.fitnesspro.service

import android.annotation.SuppressLint
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.firebase.api.firestore.documents.MessageNotificationDocument
import br.com.fitnesspro.injection.IMessagingServiceEntryPoint
import br.com.fitnesspro.notification.GenericCommunicationNotification
import br.com.fitnesspro.notification.MessageChatNotification
import br.com.fitnesspro.notification.SchedulerNotification
import br.com.fitnesspro.shared.communication.enums.notification.EnumNotificationChannel
import br.com.fitnesspro.shared.communication.notification.FitnessProNotificationData
import br.com.fitnesspro.shared.communication.notification.SchedulerNotificationCustomData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FitnessProFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val channelValue = remoteMessage.data[FitnessProNotificationData::channel.name]!!
        val channel = EnumNotificationChannel.valueOf(channelValue)

        val entryPoint = EntryPointAccessors.fromApplication(
            context = baseContext,
            entryPoint = IMessagingServiceEntryPoint::class.java
        )

        when (channel) {
            EnumNotificationChannel.NEW_MESSAGE_CHAT_CHANNEL -> {
                val json = remoteMessage.data[FitnessProNotificationData::customJSONData.name]!!
                val notificationListType = object : TypeToken<List<MessageNotificationDocument>>() {}.type
                val notifications: List<MessageNotificationDocument> = GsonBuilder().defaultGSon().fromJson(json, notificationListType)

                MessageChatNotification(baseContext, notifications).showNotification()
                deleteFirestoreNotifications(entryPoint, notifications)
            }

            EnumNotificationChannel.SCHEDULER_CHANNEL -> {
                val json = remoteMessage.data[FitnessProNotificationData::customJSONData.name]!!
                val data = GsonBuilder().defaultGSon().fromJson(json, SchedulerNotificationCustomData::class.java)

                CoroutineScope(Dispatchers.IO).launch {
                    val schedulerExists = entryPoint.getSchedulerRepository().hasSchedulerWithId(data.schedulerId)
                    val notification = SchedulerNotification(baseContext, data, schedulerExists)

                    notification.showNotification(
                        title = remoteMessage.data[FitnessProNotificationData::title.name]!!,
                        message = remoteMessage.data[FitnessProNotificationData::message.name]!!
                    )
                }
            }

            EnumNotificationChannel.GENERIC_COMMUNICATION_CHANNEL -> {
                GenericCommunicationNotification(baseContext).showNotification(
                    title = remoteMessage.data[FitnessProNotificationData::title.name]!!,
                    message = remoteMessage.data[FitnessProNotificationData::message.name]!!
                )
            }
        }
    }

    private fun deleteFirestoreNotifications(
        entryPoint: IMessagingServiceEntryPoint,
        notifications: List<MessageNotificationDocument>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            entryPoint.getPersonRepository().getAuthenticatedTOPerson()?.id?.let { personId ->
                entryPoint.getFirestoreChatRepository().deleteNotificationsAfterReceive(
                    authenticatedPersonId = personId,
                    ids = notifications.map { it.id }
                )
            }
        }
    }
}