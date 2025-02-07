package br.com.fitnesspro.core.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.fitnesspro.core.R

object NotificationHelper {

    private const val CHANNEL_ID = "chat_notifications"
    private const val CHANNEL_NAME = "Chat Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for chat messages"

    /**
     * Exibe uma notificação de chat.
     *
     * @param context Contexto da aplicação
     * @param title Título da notificação (ex: nome do remetente)
     * @param message Conteúdo da mensagem
     */
    @SuppressLint("MissingPermission")
    fun showChatNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_health)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
