package br.com.fitnesspro.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import br.com.fitnesspro.MainActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.notification.FitnessProNotification
import br.com.fitnesspro.scheduler.ui.navigation.CompromiseScreenArgs
import br.com.fitnesspro.scheduler.ui.navigation.SchedulerDetailsScreenArgs
import br.com.fitnesspro.scheduler.ui.navigation.getCompromiseScreenDeepLinkUri
import br.com.fitnesspro.scheduler.ui.navigation.getSchedulerDetailsScreenDeepLinkUri
import br.com.fitnesspro.scheduler.ui.navigation.getSchedulerScreenDeepLinkUri
import br.com.fitnesspro.shared.communication.notification.SchedulerNotificationCustomData

class SchedulerNotification(
    context: Context,
    private val customData: SchedulerNotificationCustomData,
    private val schedulerExists: Boolean
): FitnessProNotification(context) {

    override fun getChannelId(): String = SCHEDULER_CHANNEL_ID

    override fun getChannelName(): String = context.getString(R.string.scheduler_notification_channel_name)

    override fun getChannelDescription(): String = context.getString(R.string.scheduler_notification_channel_description)

    override fun getImportance(): Int = NotificationManager.IMPORTANCE_HIGH

    override fun onBuildNotification(builder: NotificationCompat.Builder) {
        super.onBuildNotification(builder)

        val deepLinkPendingIntent = getPendingIntent()
        builder.setContentIntent(deepLinkPendingIntent)
    }

    private fun getPendingIntent(): PendingIntent? {
        val uri = getNavigationDeeplinkUri()

        val deepLinkIntent = Intent(Intent.ACTION_VIEW, uri, context, MainActivity::class.java)

        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private fun getNavigationDeeplinkUri(): Uri {
        return when {
            customData.recurrent -> {
                getSchedulerScreenDeepLinkUri()
            }

            schedulerExists.not() -> {
                val args = SchedulerDetailsScreenArgs(customData.schedulerDate)
                getSchedulerDetailsScreenDeepLinkUri(args)
            }

            else -> {
                val compromiseArgs = CompromiseScreenArgs(
                    recurrent = false,
                    date = customData.schedulerDate,
                    schedulerId = customData.schedulerId
                )

                getCompromiseScreenDeepLinkUri(compromiseArgs)
            }
        }
    }

    companion object {
        const val SCHEDULER_CHANNEL_ID = "scheduler_channel_id"
    }

}