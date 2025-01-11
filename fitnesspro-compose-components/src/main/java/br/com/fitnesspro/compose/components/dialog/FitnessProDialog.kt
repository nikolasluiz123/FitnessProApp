package br.com.fitnesspro.compose.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.menu.ITupleListItem
import br.com.fitnesspro.core.theme.DialogTitleTextStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_800
import kotlinx.coroutines.flow.flowOf

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
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            text = {
                Box(modifier = Modifier.verticalScroll(state = scrollState)) {
                    Text(text = message, style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                when (type) {
                    EnumDialogType.ERROR -> {
                        DialogTextButton(R.string.label_ok, onDismissRequest, onConfirm)
                    }

                    EnumDialogType.CONFIRMATION -> {
                        DialogTextButton(R.string.label_confirm, onDismissRequest, onConfirm)
                    }
                }
            },
            dismissButton = {
                when (type) {
                    EnumDialogType.ERROR -> {
                        // Dialog do tipo Error não tem botão negativo.
                    }

                    EnumDialogType.CONFIRMATION -> {
                        DialogTextButton(R.string.label_cancel, onDismissRequest, onCancel)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            textContentColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun DialogTextButton(labelResId: Int, onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    TextButton(
        onClick = {
            onDismissRequest()
            onConfirm()
        }
    ) {
        Text(text = stringResource(id = labelResId))
    }
}

@Composable
fun <T : ITupleListItem> FitnessProPagedListDialog(
    dialogTitle: String,
    pagingItems: LazyPagingItems<T>,
    onDismissRequest: () -> Unit = { },
    onSimpleFilterChange: (String) -> Unit  = { },
    simpleFilterPlaceholderResId: Int,
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
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = dialogTitle,
                    style = DialogTitleTextStyle,
                    color = GREY_800,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                SimpleFilter(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholderResId = simpleFilterPlaceholderResId,
                    onSimpleFilterChange = {
                        filterText = it
                        onSimpleFilterChange(it)
                    },
                    expanded = isFilterExpanded,
                    onExpandedChange = { isFilterExpanded = it }
                ) {
                    PagedListDialog(pagingItems, itemLayout)
                }

                Spacer(modifier = Modifier.height(8.dp))

                PagedListDialog(pagingItems, itemLayout)
            }
        }
    }
}

@Composable
private fun <T : ITupleListItem> PagedListDialog(
    pagingItems: LazyPagingItems<T>,
    itemLayout: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (pagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is LoadState.Error -> {
                item {
                    Text(
                        text = stringResource(R.string.paged_list_dialog_erros_load_items),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            is LoadState.NotLoading -> {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey(),
                    contentType = pagingItems.itemContentType()
                ) { index ->
                    pagingItems[index]?.let { item ->
                        itemLayout(item)
                    }
                }
            }
        }

        when (pagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is LoadState.Error -> {
                item {
                    Text(
                        text = stringResource(R.string.paged_list_dialog_erros_load_new_items),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {}
        }
    }
}

@Preview
@Composable
private fun FitnessProDialogMessageErrorPreview() {
    FitnessProTheme {
        Surface {
            FitnessProMessageDialog(
                type = EnumDialogType.ERROR,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de erro"
            )
        }
    }
}

@Preview
@Composable
private fun FitnessProDialogMessageConfirmationPreview() {
    FitnessProTheme {
        Surface {
            FitnessProMessageDialog(
                type = EnumDialogType.CONFIRMATION,
                show = true,
                onDismissRequest = { },
                message = "Mensagem de confirmação"
            )
        }
    }
}

@Preview
@Composable
private fun FitnessProDialogPagedListPreview() {
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