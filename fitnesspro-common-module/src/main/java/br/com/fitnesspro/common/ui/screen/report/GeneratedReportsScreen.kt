package br.com.fitnesspro.common.ui.screen.report

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.android.ui.compose.components.list.LazyVerticalList
import br.com.android.ui.compose.components.loading.BaseLinearProgressIndicator
import br.com.android.ui.compose.components.simplefilter.SimpleFilter
import br.com.android.ui.compose.components.styles.SnackBarTextStyle
import br.com.android.ui.compose.components.styles.ValueTextStyle
import br.com.android.ui.compose.components.topbar.SimpleTopAppBar
import br.com.core.android.compose.utils.extensions.openPDFReader
import br.com.core.android.utils.media.FileUtils
import br.com.fitnesspro.common.ui.screen.report.callback.OnInactivateAllReportsClick
import br.com.fitnesspro.common.ui.screen.report.callback.OnInactivateReportClick
import br.com.fitnesspro.common.ui.state.GeneratedReportsUIState
import br.com.fitnesspro.common.ui.viewmodel.GeneratedReportsViewModel
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.core.theme.FitnessProTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GeneratedReportsScreen(
    viewModel: GeneratedReportsViewModel,
    onNavigateBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GeneratedReportsScreen(
        state = state,
        onNavigateBackClick = onNavigateBackClick,
        onInactivateAllReportsClick = viewModel::onInactivateAllReportsClick,
        onInactivateReportClick = viewModel::onInactivateReportClick,
        onUpdateReports = viewModel::onUpdateReports
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedReportsScreen(
    state: GeneratedReportsUIState = GeneratedReportsUIState(),
    onNavigateBackClick: () -> Unit = {},
    onInactivateAllReportsClick: OnInactivateAllReportsClick? = null,
    onInactivateReportClick: OnInactivateReportClick? = null,
    onUpdateReports: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onNavigateBackClick,
                actions = {
                    IconButtonDelete(
                        iconModifier = Modifier.size(24.dp),
                        onClick = {
                            onInactivateAllReportsClick?.onExecute(
                                onSuccess = {
                                    state.onToggleLoading()

                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.generated_reports_all_reports_deleted_success_message)
                                        )
                                    }
                                }
                            )
                        }
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        }
    ) { paddings ->
        Column(
            Modifier
                .padding(paddings)
                .consumeWindowInsets(paddings)
                .fillMaxSize()
        ) {
            LaunchedEffect(Unit) {
                onUpdateReports()
            }

            FitnessProMessageDialog(state.messageDialogState)

            SimpleFilter(
                modifier = Modifier.fillMaxWidth(),
                state = state.simpleFilterState,
                placeholderResId = R.string.generated_reports_simple_filter_placeholder
            ) {
                CustomHeader(state)

                ReportsList(
                    state = state,
                    context = context,
                    onInactivateReportClick = onInactivateReportClick,
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState
                )
            }

            CustomHeader(state)
            BaseLinearProgressIndicator(state.showLoading)

            ReportsList(
                state = state,
                context = context,
                onInactivateReportClick = onInactivateReportClick,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
private fun CustomHeader(state: GeneratedReportsUIState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            thickness = 0.5.dp
        )

        Text(
            text = stringResource(
                br.com.fitnesspro.common.R.string.generated_reports_label_storage_size,
                state.storageSize
            ),
            style = ValueTextStyle,
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 20.dp)
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun ReportsList(
    state: GeneratedReportsUIState,
    context: Context,
    onInactivateReportClick: OnInactivateReportClick?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    LazyVerticalList(
        emptyMessageResId = R.string.generated_reports_empty_message,
        items = state.reports,
        itemList = { toReport ->
            GeneratedReportItem(
                toReport = toReport,
                onItemClick = {
                    if (FileUtils.getFileExists(it.filePath!!)) {
                        context.openPDFReader(it.filePath!!)
                    } else {
                        state.messageDialogState.onShowDialog?.showErrorDialog(context.getString(br.com.android.pdf.generator.R.string.report_file_not_found_message))
                    }
                },
                onDeleteClick = {
                    onInactivateReportClick?.onExecute(
                        report = it,
                        onSuccess = {
                            state.onToggleLoading()

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.generated_reports_report_deleted_success_message)
                                )
                            }
                        }
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun GeneratedReportsScreenEmptyDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            GeneratedReportsScreen(
                state = generatedReportsEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun GeneratedReportsScreenDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            GeneratedReportsScreen(
                state = generatedReportsState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun GeneratedReportsScreenEmptyPreview() {
    FitnessProTheme {
        Surface {
            GeneratedReportsScreen(
                state = generatedReportsEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun GeneratedReportsScreenPreview() {
    FitnessProTheme {
        Surface {
            GeneratedReportsScreen(
                state = generatedReportsState
            )
        }
    }
}