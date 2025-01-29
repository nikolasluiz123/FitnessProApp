package br.com.fitnesspro.common.ui.screen.registeruser

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.bottomsheet.registeruser.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.common.ui.screen.registeruser.callback.OnAcademyItemClick
import br.com.fitnesspro.common.ui.screen.registeruser.callback.OnAddAcademy
import br.com.fitnesspro.common.ui.screen.registeruser.callback.OnSaveUserClick
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_ADD
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_SAVE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen
import br.com.fitnesspro.common.ui.state.RegisterUserUIState
import br.com.fitnesspro.common.ui.viewmodel.RegisterUserViewModel
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.tabs.FitnessProHorizontalPager
import br.com.fitnesspro.compose.components.tabs.FitnessProTabRow
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
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
        onSaveUserClick = viewModel::saveUser,
        onUpdateAcademies = viewModel::updateAcademies
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserScreen(
    state: RegisterUserUIState = RegisterUserUIState(),
    onBackClick: () -> Unit = { },
    onAddAcademyClick: OnAddAcademy? = null,
    onAcademyItemClick: OnAcademyItemClick? = null,
    onSaveUserClick: OnSaveUserClick? = null,
    onUpdateAcademies: () -> Unit = { }
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                    if (state.tabState.selectedTab.enum == EnumTabsRegisterUserScreen.GENERAL) {
                        FloatingActionButtonSave(
                            modifier = Modifier.testTag(REGISTER_USER_SCREEN_FAB_SAVE.name),
                            onClick = {
                                onSaveUserClick?.onExecute(
                                    onSaved = {
                                        showSaveSuccessMessage(coroutineScope, snackbarHostState, context)
                                    }
                                )
                            }
                        )
                    } else {
                        FloatingActionButtonAdd(
                            modifier = Modifier.testTag(REGISTER_USER_SCREEN_FAB_ADD.name),
                            onClick = {
                                onAddAcademyClick?.onExecute(
                                    args = br.com.fitnesspro.common.ui.navigation.RegisterAcademyScreenArgs(
                                        personId = state.toPerson.id!!,
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

            FitnessProMessageDialog(state = state.messageDialogState)

            val pagerState = rememberPagerState(pageCount = state.tabState::tabsSize)

            FitnessProTabRow(
                modifier = Modifier.constrainAs(tabRowRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
                tabState = state.tabState,
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
                tabState = state.tabState
            ) { index ->
                when (index) {
                    EnumTabsRegisterUserScreen.GENERAL.index -> {
                        RegisterUserTabGeneral(
                            state = state,
                            onDone = {
                                onSaveUserClick?.onExecute {
                                    showSaveSuccessMessage(coroutineScope, snackbarHostState, context)
                                }
                            }
                        )
                    }

                    EnumTabsRegisterUserScreen.ACADEMY.index -> {
                        RegisterUserTabAcademies(
                            state = state,
                            onAcademyItemClick = onAcademyItemClick,
                            onUpdateAcademies = onUpdateAcademies
                        )
                    }
                }
            }
        }
    }
}

private fun showSaveSuccessMessage(
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
    FitnessProTheme {
        Surface {
            RegisterUserScreen(
                state = RegisterUserUIState(
                    title = "Título",
                    subtitle = "Subtítulo",
                    context = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
                    tabState = TabState(
                        tabs = mutableListOf(
                            Tab(
                                enum = EnumTabsRegisterUserScreen.GENERAL,
                                selected = true,
                                enabled = true
                            ),
                            Tab(
                                enum = EnumTabsRegisterUserScreen.ACADEMY,
                                selected = false,
                                enabled = false
                            )
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
    FitnessProTheme {
        Surface {
            RegisterUserScreen(
                state = RegisterUserUIState(
                    title = "Título",
                    subtitle = "Subtítulo",
                    context = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
                    tabState = TabState(
                        tabs = mutableListOf(
                            Tab(
                                enum = EnumTabsRegisterUserScreen.GENERAL,
                                selected = false,
                                enabled = true
                            ),
                            Tab(
                                enum = EnumTabsRegisterUserScreen.ACADEMY,
                                selected = true,
                                enabled = true
                            )
                        )
                    )
                )
            )
        }
    }
}