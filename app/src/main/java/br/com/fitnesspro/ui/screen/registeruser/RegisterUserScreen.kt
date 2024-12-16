package br.com.fitnesspro.ui.screen.registeruser

import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.dialog.FitnessProDialog
import br.com.fitnesspro.compose.components.tabs.FitnessProHorizontalPager
import br.com.fitnesspro.compose.components.tabs.FitnessProTabRow
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.navigation.RegisterAcademyScreenArgs
import br.com.fitnesspro.ui.screen.registeruser.callback.OnAcademyItemClick
import br.com.fitnesspro.ui.screen.registeruser.callback.OnAddAcademy
import br.com.fitnesspro.ui.screen.registeruser.callback.OnSaveUserClick
import br.com.fitnesspro.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen
import br.com.fitnesspro.ui.state.RegisterUserUIState
import br.com.fitnesspro.ui.viewmodel.RegisterUserViewModel
import br.com.market.market.compose.components.button.fab.FloatingActionButtonSave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegisterUserScreen(
    viewModel: RegisterUserViewModel,
    onBackClick: () -> Unit,
    onAddAcademyClick: OnAddAcademy,
    onAcademyItemClick: OnAcademyItemClick
) {
    val state by viewModel.uiState.collectAsState()

    RegisterUserScreen(
        state = state,
        onBackClick = onBackClick,
        onAddAcademyClick = onAddAcademyClick,
        onAcademyItemClick = onAcademyItemClick,
        onSaveUserClick = viewModel::saveUser
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserScreen(
    state: RegisterUserUIState = RegisterUserUIState(),
    onBackClick: () -> Unit = { },
    onAddAcademyClick: OnAddAcademy? = null,
    onAcademyItemClick: OnAcademyItemClick? = null,
    onSaveUserClick: OnSaveUserClick? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tabs by remember { derivedStateOf { state.tabs } }
    val selectedTab = tabs.first { it.selected.value }

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title!!,
                subtitle = state.subtitle,
                showMenuWithLogout = false,
                onBackClick = onBackClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        },
        bottomBar = {
            FitnessProBottomAppBar(
                floatingActionButton = {
                    if (selectedTab.enum == EnumTabsRegisterUserScreen.GENERAL) {
                        FloatingActionButtonSave(
                            onClick = {
                                onSaveUserClick?.onExecute(
                                    onSaved = {
                                        showSuccessMessage(coroutineScope, snackbarHostState, context)
                                    }
                                )
                            }
                        )
                    } else {
                        FloatingActionButtonAdd(
                            onClick = {
                                onAddAcademyClick?.onExecute(
                                    args = RegisterAcademyScreenArgs(
                                        personId = state.toPerson?.id!!,
                                        context = state.context!!
                                    )
                                )
                            }
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val (tabRowRef, horizontalPagerRef) = createRefs()

            FitnessProDialog(
                type = state.dialogType,
                show = state.showDialog,
                onDismissRequest = { state.onHideDialog() },
                message = state.dialogMessage
            )

            val pagerState = rememberPagerState(pageCount = state.tabs::size)

            FitnessProTabRow(
                modifier = Modifier.constrainAs(tabRowRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
                tabs = tabs,
                coroutineScope = coroutineScope,
                pagerState = pagerState
            )

            FitnessProHorizontalPager(
                modifier = Modifier.constrainAs(horizontalPagerRef) {
                    start.linkTo(parent.start)
                    top.linkTo(tabRowRef.bottom)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)

                    height = Dimension.fillToConstraints
                },
                pagerState = pagerState,
                tabs = tabs
            ) { index ->
                when (index) {
                    EnumTabsRegisterUserScreen.GENERAL.index -> {
                        RegisterUserTabGeneral(
                            state = state,
                            onDone = {
                                onSaveUserClick?.onExecute {
                                    showSuccessMessage(coroutineScope, snackbarHostState, context)
                                }
                            }
                        )
                    }

                    EnumTabsRegisterUserScreen.ACADEMY.index -> {
                        RegisterUserTabAcademies(
                            state = state,
                            onAcademyItemClick = onAcademyItemClick
                        )
                    }
                }
            }
        }
    }
}

private fun showSuccessMessage(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.register_user_screen_success_message)
        )
    }
}

@Preview
@Composable
private fun RegisterUserScreenTabGeneralPreview() {
    val tab1Selected = remember { mutableStateOf(true) }
    val tab2Selected = remember { mutableStateOf(false) }

    FitnessProTheme {
        Surface {
            RegisterUserScreen(
                state = RegisterUserUIState(
                    title = "Título",
                    subtitle = "Subtítulo",
                    context = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
                    tabs = mutableListOf(
                        Tab(
                            enum = EnumTabsRegisterUserScreen.GENERAL,
                            selected = tab1Selected,
                            isEnabled = { true }
                        ),
                        Tab(
                            enum = EnumTabsRegisterUserScreen.ACADEMY,
                            selected = tab2Selected,
                            isEnabled = { false }
                        )
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun RegisterUserScreenTabAcademiesPreview() {
    val tab1Selected = remember { mutableStateOf(false) }
    val tab2Selected = remember { mutableStateOf(true) }

    FitnessProTheme {
        Surface {
            RegisterUserScreen(
                state = RegisterUserUIState(
                    title = "Título",
                    subtitle = "Subtítulo",
                    context = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
                    tabs = mutableListOf(
                        Tab(
                            enum = EnumTabsRegisterUserScreen.GENERAL,
                            selected = tab1Selected,
                            isEnabled = { true }
                        ),
                        Tab(
                            enum = EnumTabsRegisterUserScreen.ACADEMY,
                            selected = tab2Selected,
                            isEnabled = { true }
                        )
                    )
                )
            )
        }
    }
}