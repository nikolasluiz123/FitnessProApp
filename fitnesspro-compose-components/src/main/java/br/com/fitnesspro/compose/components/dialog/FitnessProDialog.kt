package br.com.fitnesspro.compose.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.buttons.DefaultDialogTextButton
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTags
import br.com.fitnesspro.compose.components.fields.state.DialogListState
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListState
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.PagedLazyVerticalList
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.enums.EnumDialogType.CONFIRMATION
import br.com.fitnesspro.core.enums.EnumDialogType.ERROR
import br.com.fitnesspro.core.enums.EnumDialogType.INFORMATION
import br.com.fitnesspro.core.menu.ITupleListItem
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.theme.DialogTitleTextStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.flow.flowOf


@Composable
fun FitnessProMessageDialog(state: MessageDialogState) {
    FitnessProMessageDialog(
        type = state.dialogType,
        show = state.showDialog,
        onDismissRequest = state.onHideDialog,
        message = state.dialogMessage,
        onConfirm = state.onConfirm,
        onCancel = state.onCancel
    )
}

/**
 * Dialog genérica para exibir qualquer mensagem necessária de acordo com as regras da tela
 * que estiver sendo implementada.
 *
 * @param type Tipo da Dialog, baseado nesse tipo são feitas configurações específicas.
 * @param show Indica se a Dialog deve ser exibida.
 * @param onDismissRequest Callback para fechar a Dialog.
 * @param message Mensagem a ser exibida na Dialog.
 * @param onConfirm Callback para confirmar a Dialog.
 * @param onCancel Callback para cancelar a Dialog.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FitnessProMessageDialog(
    type: EnumDialogType,
    show: Boolean,
    onDismissRequest: () -> Unit,
    message: String,
    onConfirm: () -> Unit = { },
    onCancel: () -> Unit = { }
) {
    val scrollState = rememberScrollState()

    if (show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = stringResource(type.titleResId),
                    style = DialogTitleTextStyle
                )
            },
            text = {
                Box(modifier = Modifier.verticalScroll(state = scrollState)) {
                    Text(
                        text = message,
                        style = ValueTextStyle
                    )
                }
            },
            confirmButton = {
                when (type) {
                    ERROR, INFORMATION -> {
                        DefaultDialogTextButton(
                            labelResId = R.string.label_ok,
                            onClick = {
                                Firebase.analytics.logButtonClick(
                                    EnumFitnessProMessageDialogTags.FITNESS_PRO_MESSAGE_DIALOG_OK_BUTTON
                                )
                                onDismissRequest()
                                onConfirm()
                            }
                        )
                    }

                    CONFIRMATION -> {
                        DefaultDialogTextButton(
                            labelResId = R.string.label_confirm,
                            onClick = {
                                Firebase.analytics.logButtonClick(
                                    EnumFitnessProMessageDialogTags.FITNESS_PRO_MESSAGE_DIALOG_CONFIRM_BUTTON
                                )
                                onDismissRequest()
                                onConfirm()
                            }
                        )
                    }
                }
            },
            dismissButton = {
                when (type) {
                    CONFIRMATION -> {
                        DefaultDialogTextButton(
                            labelResId = R.string.label_cancel,
                            onClick = {
                                Firebase.analytics.logButtonClick(
                                    EnumFitnessProMessageDialogTags.FITNESS_PRO_MESSAGE_DIALOG_CANCEL_BUTTON
                                )
                                onDismissRequest()
                                onCancel()
                            }
                        )
                    }

                    ERROR, INFORMATION -> {}
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            textContentColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun <T : ITupleListItem> FitnessProPagedListDialog(
    state: PagedDialogListState<T>,
    simpleFilterPlaceholderResId: Int,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit
) {
    FitnessProPagedListDialog(
        dialogTitle = state.dialogTitle,
        pagingItems = state.dataList.collectAsLazyPagingItems(),
        onDismissRequest = state.onHide,
        onSimpleFilterChange = state.onSimpleFilterChange,
        simpleFilterPlaceholderResId = simpleFilterPlaceholderResId,
        emptyMessage = emptyMessage,
        itemLayout = itemLayout
    )
}

@Composable
fun <T : ITupleListItem> FitnessProListDialog(
    state: DialogListState<T>,
    simpleFilterPlaceholderResId: Int,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit
) {
    FitnessProListDialog(
        dialogTitle = state.dialogTitle,
        items = state.dataList,
        onDismissRequest = state.onHide,
        onSimpleFilterChange = state.onSimpleFilterChange,
        simpleFilterPlaceholderResId = simpleFilterPlaceholderResId,
        emptyMessage = emptyMessage,
        itemLayout = itemLayout
    )
}

@Composable
fun <T : ITupleListItem> FitnessProPagedListDialog(
    dialogTitle: String,
    pagingItems: LazyPagingItems<T>,
    onDismissRequest: () -> Unit = { },
    onSimpleFilterChange: (String) -> Unit  = { },
    simpleFilterPlaceholderResId: Int,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit
) {
    var filterText by remember { mutableStateOf("") }
    var isFilterExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = dialogTitle,
                    style = DialogTitleTextStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                SimpleFilter(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholderResId = simpleFilterPlaceholderResId,
                    quickFilter = filterText,
                    onSimpleFilterChange = {
                        filterText = it
                        onSimpleFilterChange(it)
                    },
                    expanded = isFilterExpanded,
                    onExpandedChange = { isFilterExpanded = it }
                ) {
                    PagedListDialog(pagingItems, emptyMessage, itemLayout)
                }

                Spacer(modifier = Modifier.height(8.dp))

                PagedListDialog(pagingItems, emptyMessage, itemLayout)
            }
        }
    }
}

@Composable
fun <T : ITupleListItem> FitnessProListDialog(
    dialogTitle: String,
    items: List<T>,
    onDismissRequest: () -> Unit = { },
    onSimpleFilterChange: (String) -> Unit  = { },
    simpleFilterPlaceholderResId: Int,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit
) {
    var filterText by remember { mutableStateOf("") }
    var isFilterExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = dialogTitle,
                    style = DialogTitleTextStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                SimpleFilter(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholderResId = simpleFilterPlaceholderResId,
                    quickFilter = filterText,
                    onSimpleFilterChange = {
                        filterText = it
                        onSimpleFilterChange(it)
                    },
                    expanded = isFilterExpanded,
                    onExpandedChange = { isFilterExpanded = it }
                ) {
                    ListDialog(items, emptyMessage, itemLayout)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ListDialog(items, emptyMessage, itemLayout)
            }
        }
    }
}

@Composable
private fun <T : ITupleListItem> PagedListDialog(
    pagingItems: LazyPagingItems<T>,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit
) {
    PagedLazyVerticalList(
        modifier = Modifier.fillMaxWidth(),
        pagingItems = pagingItems,
        itemLayout = itemLayout,
        emptyMessageResId = emptyMessage,
        emptyStateTextColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun <T : ITupleListItem> ListDialog(
    items: List<T>,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = emptyMessage),
                style = LabelTextStyle,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = items) { item ->
                itemLayout(item)
            }
        }
    }

}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogMessageErrorPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProMessageDialog(
                type = ERROR,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de erro"
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogMessageConfirmationPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProMessageDialog(
                type = CONFIRMATION,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de confirmação"
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogMessageInformationPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProMessageDialog(
                type = INFORMATION,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de informação"
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogPagedListPreviewDark() {
    val fakeList = PagingData.from(
        listOf(
            TupleExample(
                id = "1",
                name = "Exemplo 1"
            ),
            TupleExample(
                id = "2",
                name = "Exemplo 2"
            )
        ),
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
        )
    )

    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProPagedListDialog(
                dialogTitle = "Lista de Exemplo",
                pagingItems = flowOf(fakeList).collectAsLazyPagingItems(),
                emptyMessage = R.string.paged_list_dialog_empty_message,
                simpleFilterPlaceholderResId = R.string.label_placeholder_example,
                itemLayout = {
                    Text(text = it.name)
                }
            )
        }
    }
}


@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogMessageErrorPreviewLight() {
    FitnessProTheme {
        Surface {
            FitnessProMessageDialog(
                type = ERROR,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de erro"
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogMessageConfirmationPreviewLight() {
    FitnessProTheme {
        Surface {
            FitnessProMessageDialog(
                type = CONFIRMATION,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de confirmação"
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogMessageInformationPreviewLight() {
    FitnessProTheme {
        Surface {
            FitnessProMessageDialog(
                type = INFORMATION,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de informação"
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDialogPagedListPreviewLight() {
    val fakeList = PagingData.from(
        listOf(
            TupleExample(
                id = "1",
                name = "Exemplo 1"
            ),
            TupleExample(
                id = "2",
                name = "Exemplo 2"
            )
        ),
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
        )
    )

    FitnessProTheme {
        Surface {
            FitnessProPagedListDialog(
                dialogTitle = "Lista de Exemplo",
                pagingItems = flowOf(fakeList).collectAsLazyPagingItems(),
                emptyMessage = R.string.paged_list_dialog_empty_message,
                simpleFilterPlaceholderResId = R.string.label_placeholder_example,
                itemLayout = {
                    Text(text = it.name)
                }
            )
        }
    }
}

class TupleExample(
    val id: String,
    val name: String
) : ITupleListItem {
    override fun getLabel() = name
}