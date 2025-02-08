package br.com.fitnesspro.scheduler.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.worker.FitnessProCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.notification.NewMessageChatNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NewChatMessageNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestoreChatRepository: FirestoreChatRepository,
    private val userRepository: UserRepository
) : FitnessProCoroutineWorker(context, workerParams) {

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }

    override suspend fun onWork() {
        userRepository.getAuthenticatedTOPerson()?.let { authenticatedPerson ->
            firestoreChatRepository.addMessagesNotificationListener(
                authenticatedPersonId = authenticatedPerson.id!!,
                onSuccess = { messages ->
                    val notification = NewMessageChatNotification(
                        context = context,
                        messages = messages
                    )

                    notification.showNotification(
                        title = context.getString(R.string.new_message_chat_notification_title),
                        message = context.getString(R.string.new_message_chat_notification_message)
                    )
                },
                onError = { exception ->
                    onError(exception)
                    exception.printStackTrace()
                }
            )
        }
    }

}
