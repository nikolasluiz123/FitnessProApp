package br.com.fitnesspro.notification

import android.app.NotificationManager
import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.notification.FitnessProNotification

class SchedulerNotification(context: Context): FitnessProNotification(context) {

    override fun getChannelId(): String = SCHEDULER_CHANNEL_ID

    override fun getChannelName(): String = context.getString(R.string.scheduler_notification_channel_name)

    override fun getChannelDescription(): String = context.getString(R.string.scheduler_notification_channel_description)

    override fun getNotificationId(): Int = SCHEDULER_NOTIFICATION_ID

    override fun getImportance(): Int = NotificationManager.IMPORTANCE_HIGH

    companion object {
        const val SCHEDULER_NOTIFICATION_ID = 2
        const val SCHEDULER_CHANNEL_ID = "scheduler_channel_id"
    }

}