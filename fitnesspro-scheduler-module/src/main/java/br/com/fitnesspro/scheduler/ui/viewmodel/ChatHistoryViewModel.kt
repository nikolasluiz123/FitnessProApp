package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.paging.PagingData
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListState
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import br.com.fitnesspro.model.enums.EnumUserType.ACADEMY_MEMBER
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.state.ChatHistoryUIState
import br.com.fitnesspro.tuple.PersonTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatHistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val firestoreChatRepository: FirestoreChatRepository
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<ChatHistoryUIState> = MutableStateFlow(ChatHistoryUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    private fun initialLoadUIState() {
        _uiState.update {
            it.copy(
                title = context.getString(R.string.chat_history_title),
                messageDialogState = initializeMessageDialogState(),
                membersDialogState = initializeMembersPagedDialogList(),
                professionalsDialogState = initializeProfessionalsPagedDialogList()
            )
        }
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

    private fun initializeMembersPagedDialogList(): PagedDialogListState<PersonTuple> {
        return PagedDialogListState(
            dialogTitle = context.getString(R.string.chat_history_screen_label_members_list),
            onShow = {
                _uiState.value = _uiState.value.copy(
                    membersDialogState = _uiState.value.membersDialogState.copy(show = true)
                )
            },
            onHide = {
                _uiState.value = _uiState.value.copy(
                    membersDialogState = _uiState.value.membersDialogState.copy(show = false)
                )
            },
            onDataListItemClick = { item ->
                startChat(item.id)
                _uiState.value.membersDialogState.onHide()
            },
            onSimpleFilterChange = { filter ->
                _uiState.value = _uiState.value.copy(
                    membersDialogState = _uiState.value.membersDialogState.copy(
                        dataList = getListMembers(filter)
                    )
                )
            },
            dataList = getListMembers()
        )
    }

    private fun initializeProfessionalsPagedDialogList(): PagedDialogListState<PersonTuple> {
        return PagedDialogListState(
            dialogTitle = context.getString(R.string.chat_history_screen_label_professionals_list),
            onShow = {
                _uiState.value = _uiState.value.copy(
                    professionalsDialogState = _uiState.value.professionalsDialogState.copy(show = true)
                )
            },
            onHide = {
                _uiState.value = _uiState.value.copy(
                    professionalsDialogState = _uiState.value.professionalsDialogState.copy(show = false)
                )
            },
            onDataListItemClick = { item ->
                startChat(item.id)
                _uiState.value.professionalsDialogState.onHide()
            },
            onSimpleFilterChange = { filter ->
                _uiState.value = _uiState.value.copy(
                    professionalsDialogState = _uiState.value.professionalsDialogState.copy(
                        dataList = getListProfessionals(filter)
                    )
                )
            },
            dataList = getListProfessionals()
        )
    }

    private fun getListMembers(filter: String = ""): Flow<PagingData<PersonTuple>> {
        return userRepository.getListTOPersonWithUserType(
            types = listOf(ACADEMY_MEMBER),
            simpleFilter = filter,
            personsForSchedule = false
        ).flow
    }

    private fun getListProfessionals(filter: String = ""): Flow<PagingData<PersonTuple>> {
        return userRepository.getListTOPersonWithUserType(
            types = listOf(NUTRITIONIST, PERSONAL_TRAINER),
            simpleFilter = filter,
            personsForSchedule = false
        ).flow
    }

    private fun startChat(selectedPersonId: String) {
        launch {
            val authenticatedTOPerson = userRepository.getAuthenticatedTOPerson()!!
            val selectedTOPerson = userRepository.getTOPersonById(selectedPersonId)

            firestoreChatRepository.startChat(
                senderPerson = authenticatedTOPerson,
                receiverPerson = selectedTOPerson
            )
        }
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val authenticatedPerson = userRepository.getAuthenticatedTOPerson()!!

            _uiState.update {
                it.copy(
                    history = getHistoryList(authenticatedPerson.id!!),
                    userType = authenticatedPerson.toUser?.type!!
                )
            }
        }
    }

    private suspend fun getHistoryList(authenticatedPersonId: String): List<ChatDocument> {
        return firestoreChatRepository.getChatList(authenticatedPersonId = authenticatedPersonId)
    }

    override fun onShowError(throwable: Throwable) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(
            message = context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
        )
    }
}