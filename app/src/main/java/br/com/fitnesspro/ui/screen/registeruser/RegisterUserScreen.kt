package br.com.fitnesspro.ui.screen.registeruser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.buttons.FitnessProTextButton
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCalendar
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.transformation.DateVisualTransformation
import br.com.fitnesspro.compose.components.tabs.FitnessProHorizontalPager
import br.com.fitnesspro.compose.components.tabs.FitnessProTabRow
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.toLocalDateFormattedOnlyNumbers
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPhoneKeyboardOptions
import br.com.fitnesspro.core.keyboard.NormalTextKeyboardOptions
import br.com.fitnesspro.core.keyboard.NumberKeyboardOptions
import br.com.fitnesspro.core.keyboard.PasswordKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.state.RegisterUserUIState
import br.com.fitnesspro.ui.viewmodel.RegisterUserViewModel
import br.com.market.market.compose.components.button.fab.FloatingActionButtonSave
import kotlinx.coroutines.launch

@Composable
fun RegisterUserScreen(viewModel: RegisterUserViewModel) {
    val state by viewModel.uiState.collectAsState()

    RegisterUserScreen(
        state = state,
        onFABSaveClick = viewModel::saveUser
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RegisterUserScreen(
    state: RegisterUserUIState = RegisterUserUIState(),
    onFABSaveClick: suspend () -> Boolean = { false }
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title!!,
                subtitle = state.subtitle,
                showMenuWithLogout = false
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
                                val success = onFABSaveClick()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserTabGeneral(state: RegisterUserUIState) {
    ConstraintLayout(
        Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        val (nameRef, emailRef, passwordRef, birthDateRef, phoneRef) = createRefs()

        var openDatePickerDialog by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        OutlinedTextFieldValidation(
            field = state.name,
            label = stringResource(R.string.register_user_screen_label_name),
            modifier = Modifier.constrainAs(nameRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = NormalTextKeyboardOptions
        )

        OutlinedTextFieldValidation(
            field = state.email,
            label = stringResource(R.string.register_user_screen_label_email),
            modifier = Modifier.constrainAs(emailRef) {
                start.linkTo(parent.start)
                top.linkTo(nameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = EmailKeyboardOptions
        )

        OutlinedTextFieldValidation(
            field = state.password,
            label = stringResource(R.string.register_user_screen_label_password),
            modifier = Modifier.constrainAs(passwordRef) {
                start.linkTo(parent.start)
                top.linkTo(emailRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PasswordKeyboardOptions
        )

        OutlinedTextFieldValidation(
            field = state.birthDate,
            label = stringResource(R.string.register_user_screen_label_birth_date),
            modifier = Modifier.constrainAs(birthDateRef) {
                start.linkTo(parent.start)
                top.linkTo(passwordRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = NumberKeyboardOptions,
            trailingIcon = {
                IconButtonCalendar(
                    onClick = { openDatePickerDialog = true }
                )
            },
            visualTransformation = DateVisualTransformation()
        )

        if (state.context == EnumOptionsBottomSheetRegisterUser.TRAINER ||
            state.context == EnumOptionsBottomSheetRegisterUser.NUTRITIONIST
        ) {

            OutlinedTextFieldValidation(
                field = state.phone,
                label = stringResource(R.string.register_user_screen_label_phone),
                modifier = Modifier.constrainAs(phoneRef) {
                    start.linkTo(parent.start)
                    top.linkTo(birthDateRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
                keyboardOptions = LastPhoneKeyboardOptions
            )
        }

        if (openDatePickerDialog) {
            DatePickerDialog(
                onDismissRequest = { openDatePickerDialog = false },
                confirmButton = {
                    FitnessProTextButton(
                        label = stringResource(id = R.string.register_user_screen_button_confirm),
                        onClickListener = {
                            datePickerState.selectedDateMillis?.let {
                                state.birthDate.onChange(it.toLocalDateFormattedOnlyNumbers(EnumDateTimePatterns.DATE))
                            }

                            openDatePickerDialog = false
                        }
                    )
                },
                dismissButton = {
                    FitnessProTextButton(
                        label = stringResource(id = R.string.register_user_screen_button_cancel),
                        onClickListener = { openDatePickerDialog = false }
                    )
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun RegisterUserTabGym(
    state: RegisterUserUIState
) {
    Scaffold { padding ->
        ConstraintLayout(Modifier.padding(padding)) {

        }
    }
}

enum class EnumTabsRegisterUserScreen(val index: Int) {
    GENERAL(0),
    GYM(1)
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