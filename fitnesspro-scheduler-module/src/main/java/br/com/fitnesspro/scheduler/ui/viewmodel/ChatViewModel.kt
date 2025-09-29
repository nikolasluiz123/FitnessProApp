package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.core.utils.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.ChatArgs
import br.com.fitnesspro.scheduler.ui.navigation.chatArguments
import br.com.fitnesspro.scheduler.ui.state.ChatUIState
import br.com.fitnesspro.scheduler.usecase.scheduler.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val firestoreChatRepository: FirestoreChatRepository,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<ChatUIState> = MutableStateFlow(ChatUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[chatArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            title = context.getString(R.string.chat_screen_title),
            messageTextField = createTextFieldState(
                getCurrentState = { _uiState.value.messageTextField },
                updateState = { _uiState.value = _uiState.value.copy(messageTextField = it) },
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            )
        )
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    fun onExecuteLoad() {
        loadUIStateWithDatabaseInfos()
        addListeners()

        _uiState.value.executeLoad = false
    }

    private fun loadUIStateWithDatabaseInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(ChatArgs::class.java)!!

        launch {
            val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

            _uiState.value = _uiState.value.copy(
                subtitle = firestoreChatRepository.getPersonNameFromChat(
                    personId = authenticatedPersonId,
                    chatId = args.chatId,
                ),
                authenticatedPersonId = authenticatedPersonId
            )
        }
    }

    private fun addListeners() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ChatArgs::class.java)!!
            val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

            firestoreChatRepository.addMessagesListListener(
                authenticatedPersonId = authenticatedPersonId,
                chatId = args.chatId,
                onSuccess = { messages ->
                    _uiState.value = _uiState.value.copy(messages = messages)
                },
                onError = { exception ->
                    getErrorMessageFrom(exception)
                    onError(exception)
                }
            )

            firestoreChatRepository.addMessagesReadListener(
                authenticatedPersonId = authenticatedPersonId,
                chatId = args.chatId,
                onError = { exception ->
                    getErrorMessageFrom(exception)
                    onError(exception)
                }
            )
        }
    }

    fun sendMessage() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ChatArgs::class.java)!!
            val message = _uiState.value.messageTextField.value

            sendChatMessageUseCase(message, args.chatId)

            _uiState.value = _uiState.value.copy(
                messageTextField = _uiState.value.messageTextField.copy(value = "")
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        firestoreChatRepository.removeMessagesListListener()
        firestoreChatRepository.removeMessagesReadListener()
    }
}