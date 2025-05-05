package br.com.fitnesspro.core.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.fitnesspro.core.R

abstract class FitnessProNotification(protected val context: Context) {

    abstract fun getChannelId(): String

    abstract fun getChannelName(): String

    abstract fun getChannelDescription(): String

    abstract fun getNotificationId(): Int

    abstract fun getImportance(): Int

    open fun onBuildNotification(builder: NotificationCompat.Builder) = Unit

    @SuppressLint("MissingPermission")
    fun showNotification(title: String = "", message: String = "") {
        createNotificationChannel()

        val defaultNotificationBuilder = NotificationCompat.Builder(context, getChannelId())
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)

        onBuildNotification(defaultNotificationBuilder)

        val notification = defaultNotificationBuilder.build()

        with(NotificationManagerCompat.from(context)) {
            notify(getNotificationId(), notification)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(getChannelId(), getChannelName(), getImportance()).apply {
            description = getChannelDescription()
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}