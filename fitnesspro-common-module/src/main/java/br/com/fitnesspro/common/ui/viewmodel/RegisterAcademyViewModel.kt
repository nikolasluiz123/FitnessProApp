package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.navigation.RegisterAcademyScreenArgs
import br.com.fitnesspro.common.ui.state.RegisterAcademyUIState
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.common.usecase.academy.EnumValidatedAcademyFields
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.tuple.AcademyTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class RegisterAcademyViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val personRepository: PersonRepository,
    private val academyRepository: AcademyRepository,
    private val savePersonAcademyTimeUseCase: SavePersonAcademyTimeUseCase,
    private val globalEvents: GlobalEvents,
    savedStateHandle: SavedStateHandle
) : FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<RegisterAcademyUIState> = MutableStateFlow(RegisterAcademyUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[br.com.fitnesspro.common.ui.navigation.registerAcademyArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            academy = createPagedDialogListTextField(
                getCurrentState = { _uiState.value.academy },
                updateState = { _uiState.value = _uiState.value.copy(academy = it) },
                dialogTitle = context.getString(R.string.register_academy_screen_title_select_academy),
                getDataListFlow = ::getListAcademies,
                onDataListItemClick = { academyTuple ->
                    _uiState.value = _uiState.value.copy(
                        toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                            academyId = academyTuple.id,
                            academyName = academyTuple.name
                        )
                    )
                },
            ),
            dayWeek =  createDropDownTextFieldState(
                items = getMenuItemsDayOfWeek(),
                getCurrentState = { _uiState.value.dayWeek },
                updateState = { _uiState.value = _uiState.value.copy(dayWeek = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                            dayOfWeek = it
                        )
                    )
                }
            ),
            start = createTimePickerTextField(
                getCurrentState = { _uiState.value.start },
                updateState = { _uiState.value = _uiState.value.copy(start = it) },
                onTimeChange = {
                    _uiState.value = _uiState.value.copy(
                        toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                            timeStart = it
                        )
                    )
                }
            ),
            end = createTimePickerTextField(
                getCurrentState = { _uiState.value.end },
                updateState = { _uiState.value = _uiState.value.copy(end = it) },
                onTimeChange = {
                    _uiState.value = _uiState.value.copy(
                        toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                            timeEnd = it
                        )
                    )
                }
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(
                    showLoading = _uiState.value.showLoading.not()
                )
            }
        )
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    private fun getMenuItemsDayOfWeek(): List<MenuItem<DayOfWeek?>> = DayOfWeek.entries.map { dayOfWeek ->
        MenuItem(
            label = dayOfWeek.getFirstPartFullDisplayName(),
            value = dayOfWeek
        )
    }

    private fun getListAcademies(filter: String = ""): Flow<PagingData<AcademyTuple>> {
        return academyRepository.getAcademies(simpleFilter = filter).flow
    }

    private fun loadUIStateWithDatabaseInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(RegisterAcademyScreenArgs::class.java)!!

        launch {
            val toPerson = personRepository.getTOPersonById(personId = args.personId)
            val menuItemListAcademy = getListAcademies()
            val id = args.personAcademyTimeId ?: _uiState.value.toPersonAcademyTime.id
            val toPersonAcademyTime = id?.let {
                academyRepository.getTOPersonAcademyTimeById(personAcademyTimeId = it)
            }

            _uiState.value = _uiState.value.copy(
                title = getTitle(toPerson, toPersonAcademyTime),
                subtitle = getSubtitle(toPersonAcademyTime),
                academy = _uiState.value.academy.copy(
                    value = toPersonAcademyTime?.academyName ?: "",
                    dialogListState = _uiState.value.academy.dialogListState.copy(
                        dataList = menuItemListAcademy
                    )
                ),
                dayWeek = _uiState.value.dayWeek.copy(value = toPersonAcademyTime?.dayOfWeek?.getFirstPartFullDisplayName() ?: ""),
                start = _uiState.value.start.copy(value = toPersonAcademyTime?.timeStart?.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS) ?: ""),
                end = _uiState.value.end.copy(value = toPersonAcademyTime?.timeEnd?.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS) ?: ""),
                toPersonAcademyTime = toPersonAcademyTime ?: TOPersonAcademyTime(personId = args.personId),
                isEnabledInactivationButton = toPersonAcademyTime?.id != null
            )
        }
    }

    private fun getTitle(toPerson: TOPerson, toPersonAcademyTime: TOPersonAcademyTime?): String {
        return if (toPersonAcademyTime != null) {
            context.getString(
                R.string.register_academy_screen_title_edit,
                toPersonAcademyTime.dayOfWeek!!.getFirstPartFullDisplayName(),
                toPersonAcademyTime.timeStart!!.format(EnumDateTimePatterns.TIME),
                toPersonAcademyTime.timeEnd!!.format(EnumDateTimePatterns.TIME)
            )
        } else {
            when (toPerson.user?.type!!) {
                EnumUserType.PERSONAL_TRAINER,
                EnumUserType.NUTRITIONIST -> context.getString(R.string.register_academy_screen_title_new_work_hour)
                EnumUserType.ACADEMY_MEMBER -> context.getString(R.string.register_academy_screen_title_new_academy_member)
            }
        }
    }

    private fun getSubtitle(toPersonAcademyTime: TOPersonAcademyTime?): String? {
        return toPersonAcademyTime?.academyName
    }

    fun saveAcademy(onSuccess: () -> Unit) {
        launch {
            val validationResults = savePersonAcademyTimeUseCase.execute(_uiState.value.toPersonAcademyTime)

            if (validationResults.isEmpty()) {
                onSuccess()
                loadUIStateWithDatabaseInfos()
            } else {
                _uiState.value.onToggleLoading()
                showValidationMessages(validationResults)
            }
        }
    }

    private fun showValidationMessages(validationResults: List<FieldValidationError<EnumValidatedAcademyFields>>) {
        val dialogValidations = validationResults.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = dialogValidations.message)
            return
        }

        validationResults.forEach {
            when (it.field!!) {
                EnumValidatedAcademyFields.ACADEMY -> {
                    _uiState.value = _uiState.value.copy(
                        academy = _uiState.value.academy.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedAcademyFields.TIME_START -> {
                    _uiState.value = _uiState.value.copy(
                        start = _uiState.value.start.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedAcademyFields.TIME_END -> {
                    _uiState.value = _uiState.value.copy(
                        end = _uiState.value.end.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedAcademyFields.DAY_OF_WEEK -> {
                    _uiState.value = _uiState.value.copy(
                        dayWeek = _uiState.value.dayWeek.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }
    }

    fun inactivateAcademy(onSuccess: () -> Unit) {
        val state = _uiState.value

        state.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.register_academy_screen_message_inactivate_academy)
        ) {
            launch {
                academyRepository.inactivatePersonAcademyTime(state.toPersonAcademyTime)
                onSuccess()
            }
        }
    }

}