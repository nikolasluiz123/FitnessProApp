package br.com.fitnesspro.common.ui.viewmodel.base

import androidx.core.text.isDigitsOnly
import androidx.paging.PagingData
import br.com.android.ui.compose.components.buttons.switchbutton.state.SwitchButtonField
import br.com.android.ui.compose.components.dialog.list.DialogListState
import br.com.android.ui.compose.components.dialog.list.paged.PagedDialogListState
import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.dropdown.MenuItem
import br.com.android.ui.compose.components.fields.dropdown.getLabelOrEmptyIfNullValue
import br.com.android.ui.compose.components.fields.dropdown.state.DropDownTextField
import br.com.android.ui.compose.components.fields.text.date.state.DatePickerTextField
import br.com.android.ui.compose.components.fields.text.dialog.paged.state.PagedDialogListTextField
import br.com.android.ui.compose.components.fields.text.dialog.state.DialogListTextField
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.fields.text.time.state.TimePickerTextField
import br.com.android.ui.compose.components.simplefilter.SimpleFilterState
import br.com.android.ui.compose.components.tabs.state.Tab
import br.com.android.ui.compose.components.tabs.state.TabState
import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.core.android.utils.interfaces.ISimpleListItem
import br.com.core.utils.enums.EnumDateTimePatterns
import br.com.core.utils.extensions.format
import br.com.core.utils.extensions.parseDouble
import br.com.core.utils.extensions.parseToLocalDate
import br.com.core.utils.extensions.parseToLocalTime
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

abstract class FitnessProStatefulViewModel: FitnessProViewModel() {

    protected abstract fun initialLoadUIState()

    protected fun createMessageDialogState(
        getCurrentState: () -> MessageDialogState,
        updateState: (newState: MessageDialogState) -> Unit
    ): MessageDialogState {
        return MessageDialogState(
            onShowDialog = { type, message, onConfirm, onCancel ->
                val newState = getCurrentState().copy(
                    dialogType = type,
                    dialogMessage = message,
                    showDialog = true,
                    onConfirm = onConfirm,
                    onCancel = onCancel
                )

                updateState(newState)
            },
            onHideDialog = {
                val newState = getCurrentState().copy(showDialog = false)
                updateState(newState)
            }
        )
    }

    protected fun createTextFieldState(
        getCurrentState: () -> TextField,
        updateState: (newState: TextField) -> Unit,
        canWrite: (String) -> Boolean = { true }
    ): TextField {
        return TextField(
            onChange = {
                if (canWrite(it)) {
                    val newState = getCurrentState().copy(
                        value = it,
                        errorMessage = ""
                    )

                    updateState(newState)
                }
            }
        )
    }

    protected fun createDoubleValueTextFieldState(
        getCurrentState: () -> TextField,
        onValueChange: (newState: TextField, newValue: Double?) -> Unit
    ): TextField {
        return createTextFieldState(
            getCurrentState = getCurrentState,
            canWrite = { it.isDigitsOnly() },
            updateState = { textField ->
                onValueChange(textField, textField.value.parseDouble())
            }
        )
    }

    protected fun createIntValueTextFieldState(
        getCurrentState: () -> TextField,
        onValueChange: (newState: TextField, newValue: Int?) -> Unit
    ): TextField {
        return createTextFieldState(
            getCurrentState = getCurrentState,
            canWrite = { it.isDigitsOnly() },
            updateState = { textField ->
                onValueChange(textField, textField.value.toIntOrNull())
            }
        )
    }

    protected fun createLongValueTextFieldState(
        getCurrentState: () -> TextField,
        onValueChange: (newState: TextField, newValue: Long?) -> Unit
    ): TextField {
        return createTextFieldState(
            getCurrentState = getCurrentState,
            canWrite = { it.isDigitsOnly() },
            updateState = { textField ->
                onValueChange(textField, textField.value.toLongOrNull())
            }
        )
    }

    protected fun <T> createDropDownTextFieldState(
        items: List<MenuItem<T?>>,
        getCurrentState: () -> DropDownTextField<T>,
        updateState: (newState: DropDownTextField<T>) -> Unit,
        onItemClick: (value: T?) -> Unit
    ): DropDownTextField<T> {
        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                val newState = getCurrentState().copy(expanded = false)
                updateState(newState)
            },
            onDropDownExpandedChange = {
                val newState = getCurrentState().copy(expanded = it)
                updateState(newState)
            },
            onDataListItemClick = {
                val newState = getCurrentState().copy(
                    value = it.getLabelOrEmptyIfNullValue(),
                    errorMessage = ""
                )

                updateState(newState)
                onItemClick(it.value)
                newState.onDropDownDismissRequest()
            }
        )
    }

    protected fun createVideoGalleryState(
        title: String,
        getCurrentState: () -> VideoGalleryState,
        updateState: (newState: VideoGalleryState) -> Unit,
        isScrollEnabled: Boolean = true,
        showDeleteButton: Boolean = true
    ): VideoGalleryState {
        return VideoGalleryState(
            title = title,
            isScrollEnabled = isScrollEnabled,
            showDeleteButton = showDeleteButton,
            onViewModeChange = {
                val newState = getCurrentState().copy(viewMode = it)
                updateState(newState)
            }
        )
    }

    protected fun createSimpleFilterState(
        getCurrentState: () -> SimpleFilterState,
        updateState: (newState: SimpleFilterState) -> Unit,
        onSimpleFilterChange: (String) -> Unit
    ): SimpleFilterState {
        return SimpleFilterState(
            onSimpleFilterChange = { filterText ->
                val newState = getCurrentState().copy(quickFilter = filterText)
                updateState(newState)
                onSimpleFilterChange(filterText)
            },
            onExpandedChange = {
                val newState = getCurrentState().copy(simpleFilterExpanded = it)
                updateState(newState)
            }
        )
    }

    protected fun <T: ISimpleListItem> createPagedDialogListTextField(
        getCurrentState: () -> PagedDialogListTextField<T>,
        updateState: (newState: PagedDialogListTextField<T>) -> Unit,
        dialogTitle: String,
        onDataListItemClick: (item: T) -> Unit,
        getDataListFlow: suspend (String) -> Flow<PagingData<T>>,
        onChange: (String) -> Unit = {},
        showTrailingIcon: Boolean = true
    ): PagedDialogListTextField<T> {
        return PagedDialogListTextField(
            dialogListState = createPagedDialogListState(
                showTrailingIcon = showTrailingIcon,
                getCurrentState = { getCurrentState().dialogListState },
                updateState = {
                    val newState = getCurrentState().copy(dialogListState = it)
                    updateState(newState)
                },
                dialogTitle = dialogTitle,
                onDataListItemClick = {
                    val newState = getCurrentState().copy(value = it.getLabel(), errorMessage = "")
                    updateState(newState)

                    onDataListItemClick(it)
                },
                getDataListFlow = getDataListFlow
            ),
            onChange = { newText ->
                val newState = getCurrentState().copy(value = newText, errorMessage = "")
                onChange(newText)
                updateState(newState)
            }
        )
    }

    protected fun <T: ISimpleListItem> createPagedDialogListState(
        getCurrentState: () -> PagedDialogListState<T>,
        updateState: (newState: PagedDialogListState<T>) -> Unit,
        dialogTitle: String,
        onDataListItemClick: (item: T) -> Unit,
        getDataListFlow: suspend (String) -> Flow<PagingData<T>>,
        showTrailingIcon: Boolean = true
    ): PagedDialogListState<T> {
        return PagedDialogListState(
            showTrailingIcon = showTrailingIcon,
            dialogTitle = dialogTitle,
            onShow = {
                val newState = getCurrentState().copy(show = true)
                updateState(newState)
            },
            onHide = {
                val newState = getCurrentState().copy(show = false)
                updateState(newState)
            },
            onDataListItemClick = { item ->
                onDataListItemClick(item)
                getCurrentState().onHide()
            },
            onSimpleFilterChange = { filter ->
                launch {
                    val newState = getCurrentState().copy(dataList = getDataListFlow(filter))
                    updateState(newState)
                }
            },
        )
    }

    protected fun <T: ISimpleListItem> createDialogListTextField(
        getCurrentState: () -> DialogListTextField<T>,
        updateState: (newState: DialogListTextField<T>) -> Unit,
        dialogTitle: String,
        onDataListItemClick: (item: T) -> Unit,
        getDataList: suspend (String) -> List<T>,
        onChange: (String) -> Unit
    ): DialogListTextField<T> {
        return DialogListTextField(
            dialogListState = createDialogListState(
                getCurrentState = { getCurrentState().dialogListState },
                updateState = {
                    val newState = getCurrentState().copy(dialogListState = it)
                    updateState(newState)
                },
                dialogTitle = dialogTitle,
                onDataListItemClick = {
                    val newState = getCurrentState().copy(value = it.getLabel(), errorMessage = "")
                    updateState(newState)

                    onDataListItemClick(it)
                },
                getDataList = getDataList
            ),
            onChange = { newText ->
                val newState = getCurrentState().copy(value = newText, errorMessage = "")
                onChange(newText)
                updateState(newState)
            }
        )
    }

    protected fun <T: ISimpleListItem> createDialogListState(
        getCurrentState: () -> DialogListState<T>,
        updateState: (newState: DialogListState<T>) -> Unit,
        dialogTitle: String,
        onDataListItemClick: (item: T) -> Unit,
        getDataList: suspend (String) -> List<T>,
    ): DialogListState<T> {
        return DialogListState(
            dialogTitle = dialogTitle,
            onShow = {
                val newState = getCurrentState().copy(show = true)
                updateState(newState)
            },
            onHide = {
                val newState = getCurrentState().copy(show = false)
                updateState(newState)
            },
            onDataListItemClick = { item ->
                onDataListItemClick(item)
                getCurrentState().onHide()
            },
            onSimpleFilterChange = { filter ->
                launch {
                    val newState = getCurrentState().copy(dataList = getDataList(filter))
                    updateState(newState)
                }
            },
        )
    }

    protected fun createTimePickerTextField(
        getCurrentState: () -> TimePickerTextField,
        updateState: (newState: TimePickerTextField) -> Unit,
        onTimeChange: (LocalTime?) -> Unit
    ): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = {
                val newState = getCurrentState().copy(timePickerOpen = it)
                updateState(newState)
            },
            onTimeDismiss = {
                val newState = getCurrentState().copy(timePickerOpen = false)
                updateState(newState)
            },
            onTimeChange = { newTime ->
                val newState = getCurrentState().copy(
                    value = newTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                    errorMessage = ""
                )
                updateState(newState)
                onTimeChange(newTime)
            },
            onChange = {
                if (it.isDigitsOnly()) {
                    val newState = getCurrentState().copy(
                        value = it,
                        errorMessage = ""
                    )

                    updateState(newState)
                    onTimeChange(it.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS))
                }
            }
        )
    }

    protected fun createTabState(
        getCurrentState: () -> TabState,
        updateState: (newState: TabState) -> Unit,
        tabs: MutableList<Tab>
    ): TabState {
        return TabState(
            tabs = tabs,
            onSelectTab = { selectedTab ->
                val currentState = getCurrentState()

                val updatedTabsValue = currentState.tabs.map { tab ->
                    tab.copy(selected = tab.enum == selectedTab.enum)
                }.toMutableList()

                val newState = currentState.copy(tabs = updatedTabsValue)
                updateState(newState)
            }
        )
    }

    protected fun createDatePickerFieldState(
        getCurrentState: () -> DatePickerTextField,
        updateState: (newState: DatePickerTextField) -> Unit,
        onDateChange: (LocalDate?) -> Unit
    ): DatePickerTextField {
        return DatePickerTextField(
            onDatePickerOpenChange = { newOpen ->
                val newState = getCurrentState().copy(datePickerOpen = newOpen)
                updateState(newState)
            },
            onDateChange = { newDate ->
                val currentState = getCurrentState()

                val newState = currentState.copy(
                    value = newDate.format(EnumDateTimePatterns.DATE_ONLY_NUMBERS),
                    errorMessage = ""
                )

                updateState(newState)
                onDateChange(newDate)
                currentState.onDatePickerDismiss()
            },
            onDatePickerDismiss = {
                val newState = getCurrentState().copy(datePickerOpen = false)
                updateState(newState)
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    val newState = getCurrentState().copy(
                        value = text,
                        errorMessage = ""
                    )

                    updateState(newState)
                    onDateChange(text.parseToLocalDate(EnumDateTimePatterns.DATE_ONLY_NUMBERS))
                }
            }
        )
    }

    protected fun createSwitchButtonFieldState(
        getCurrentState: () -> SwitchButtonField,
        updateState: (newState: SwitchButtonField) -> Unit,
    ): SwitchButtonField {
        return SwitchButtonField(
            onCheckedChange = { checked ->
                val newState = getCurrentState().copy(checked = checked)
                updateState(newState)
            }
        )
    }
}