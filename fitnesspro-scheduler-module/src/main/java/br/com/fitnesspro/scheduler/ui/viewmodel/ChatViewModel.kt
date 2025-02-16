package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.state.MessageDialogState
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
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestoreChatRepository: FirestoreChatRepository,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val personRepository: PersonRepository,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<ChatUIState> = MutableStateFlow(ChatUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[chatArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()

        addListeners()
    }

    override fun onShowError(throwable: Throwable) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(
            message = context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
        )
    }

    private fun initialLoadUIState() {
        _uiState.update {
            it.copy(
                title = context.getString(R.string.chat_screen_title),
                messageTextField = initializeMessageTextField(),
                messageDialogState = initializeMessageDialogState()
            )
        }
    }

    private fun initializeMessageTextField(): TextField {
        return TextField(onChange = {
            _uiState.value = _uiState.value.copy(
                messageTextField = _uiState.value.messageTextField.copy(
                    value = it,
                    errorMessage = ""
                )
            )
        })
    }

    private fun initializeMessageDialogState(): MessageDialogState {
        return MessageDialogState(
            onShowDialog = { type, message, onConfirm, onCancel ->
                _uiState.value = _uiState.value.copy(
                    messageDialogState = _uiState.value.messageDialogState.copy(
                        dialogType = type,
                        dialogMessage = message,
                        showDialog = true,
                        onConfirm = onConfirm,
                        onCancel = onCancel
                    )
                )
            },
            onHideDialog = {
                _uiState.value = _uiState.value.copy(
                    messageDialogState = _uiState.value.messageDialogState.copy(
                        showDialog = false
                    )
                )
            }
        )
    }

    private fun loadUIStateWithDatabaseInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(ChatArgs::class.java)!!

        launch {
            val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

            _uiState.update {
                it.copy(
                    subtitle = firestoreChatRepository.getPersonNameFromChat(
                        personId = authenticatedPersonId,
                        chatId = args.chatId,
                    ),
                    authenticatedPersonId = authenticatedPersonId
                )
            }
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
                    onShowError(exception)
                    onError(exception)
                }
            )

            firestoreChatRepository.addMessagesReadListener(
                authenticatedPersonId = authenticatedPersonId,
                chatId = args.chatId,
                onError = { exception ->
                    onShowError(exception)
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