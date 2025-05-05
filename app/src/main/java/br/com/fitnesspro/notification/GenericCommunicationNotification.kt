package br.com.fitnesspro.notification

import android.app.NotificationManager
import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.notification.FitnessProNotification

class GenericCommunicationNotification(context: Context): FitnessProNotification(context) {

    override fun getChannelId(): String = GENERIC_COMMUNICATION_CHANNEL_ID

    override fun getChannelName(): String = context.getString(R.string.generic_communication_notification_channel_name)

    override fun getChannelDescription(): String = context.getString(R.string.generic_communication_notification_channel_description)

    override fun getNotificationId(): Int = GENERIC_COMMUNICATION_NOTIFICATION_ID

    override fun getImportance(): Int = NotificationManager.IMPORTANCE_DEFAULT

    companion object {
        const val GENERIC_COMMUNICATION_NOTIFICATION_ID = 3
        const val GENERIC_COMMUNICATION_CHANNEL_ID = "generic_communication_channel_id"
    }

}