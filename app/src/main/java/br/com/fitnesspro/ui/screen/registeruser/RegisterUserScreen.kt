package br.com.fitnesspro.ui.screen.registeruser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.dialog.FitnessProDialog
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.tabs.FitnessProHorizontalPager
import br.com.fitnesspro.compose.components.tabs.FitnessProTabRow
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPasswordKeyboardOptions
import br.com.fitnesspro.core.keyboard.NormalTextKeyboardOptions
import br.com.fitnesspro.core.keyboard.PasswordKeyboardOptions
import br.com.fitnesspro.core.keyboard.PersonNameKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.service.data.access.dto.user.EnumUserDTOValidationFields
import br.com.fitnesspro.ui.screen.registeruser.callback.OnServerError
import br.com.fitnesspro.ui.state.RegisterUserUIState
import br.com.fitnesspro.ui.viewmodel.RegisterUserViewModel
import br.com.market.market.compose.components.button.fab.FloatingActionButtonSave
import kotlinx.coroutines.launch

@Composable
fun RegisterUserScreen(
    viewModel: RegisterUserViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    RegisterUserScreen(
        state = state,
        onBackClick = onBackClick,
        onFABSaveClick = { onServerError ->
            viewModel.saveUser(onServerError)
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RegisterUserScreen(
    state: RegisterUserUIState = RegisterUserUIState(),
    onFABSaveClick: suspend (OnServerError) -> Boolean = { false },
    onBackClick: () -> Unit = { },
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
        floatingActionButton = {
            when (state.tabs.first(Tab::selected).index) {
                0 -> {
                    FloatingActionButtonSave(
                        onClick = {
                            coroutineScope.launch {
                                val success = onFABSaveClick { message ->
                                    showErrorDialog(state, message)
                                }

                                if (success) {
                                    snackbarHostState.showSnackbar(context.getString(R.string.register_user_screen_success_message))
                                }
                            }
                        }
                    )
                }

                1 -> {
                    FloatingActionButtonAdd(
                        onClick = {

                        }
                    )
                }
            }
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
                tabs = state.tabs,
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
                tabs = state.tabs
            ) {
                when (state.tabs.first(Tab::selected).index) {
                    0 -> {
                        RegisterUserTabGeneral(
                            state = state,
                            onDone = {
                                coroutineScope.launch {
                                    onFABSaveClick { message ->
                                        showErrorDialog(state, message)
                                    }
                                }
                            }
                        )
                    }

                    1 -> {
                        RegisterUserTabGym(state = state)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterUserTabGeneral(state: RegisterUserUIState, onDone: () -> Unit) {
    ConstraintLayout(
        Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        val (firstNameRef, lastNameRef, usernameRef, emailRef, passwordRef) = createRefs()

        OutlinedTextFieldValidation(
            field = state.firstName,
            label = stringResource(R.string.register_user_screen_label_first_name),
            modifier = Modifier.constrainAs(firstNameRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PersonNameKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.FIRST_NAME.maxLength
        )

        OutlinedTextFieldValidation(
            field = state.lastName,
            label = stringResource(R.string.register_user_screen_label_last_name),
            modifier = Modifier.constrainAs(lastNameRef) {
                start.linkTo(parent.start)
                top.linkTo(firstNameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PersonNameKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.LAST_NAME.maxLength
        )

        OutlinedTextFieldValidation(
            field = state.username,
            label = stringResource(R.string.register_user_screen_label_username),
            modifier = Modifier.constrainAs(usernameRef) {
                start.linkTo(parent.start)
                top.linkTo(lastNameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = NormalTextKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.USERNAME.maxLength
        )

        OutlinedTextFieldValidation(
            field = state.email,
            label = stringResource(R.string.register_user_screen_label_email),
            modifier = Modifier.constrainAs(emailRef) {
                start.linkTo(parent.start)
                top.linkTo(usernameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = EmailKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.EMAIL.maxLength
        )

        OutlinedTextFieldPasswordValidation(
            field = state.password,
            label = stringResource(R.string.register_user_screen_label_password),
            modifier = Modifier.constrainAs(passwordRef) {
                start.linkTo(parent.start)
                top.linkTo(emailRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            maxLength = EnumUserDTOValidationFields.PASSWORD.maxLength,
            keyboardOptions = LastPasswordKeyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            )
        )
    }
}

@Composable
fun RegisterUserTabGym(
    state: RegisterUserUIState
) {
    ConstraintLayout() {

    }
}

enum class EnumTabsRegisterUserScreen(val index: Int) {
    GENERAL(0),
    GYM(1)
}

private fun showErrorDialog(state: RegisterUserUIState, message: String) {
    state.onShowDialog?.onShow(
        type = EnumDialogType.ERROR,
        message = message,
        onConfirm = { },
        onCancel = { }
    )
}

@Preview
@Composable
private fun RegisterUserScreenPreview() {
    FitnessProTheme {
        Surface {
            RegisterUserScreen()
        }
    }
}