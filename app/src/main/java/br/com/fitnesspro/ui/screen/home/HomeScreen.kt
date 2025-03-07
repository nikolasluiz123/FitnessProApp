package br.com.fitnesspro.ui.screen.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.common.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.compose.components.buttons.SquaredButton
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonAccount
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonLogout
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonNotification
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_600
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.ui.bottomsheet.workout.BottomSheetWorkout
import br.com.fitnesspro.ui.screen.home.callbacks.OnLogoutClick
import br.com.fitnesspro.ui.screen.home.callbacks.OnNavigateToAccountInformation
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_ACCOUNT_BUTTON
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_BUTTON_MONEY
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_BUTTON_NUTRITION
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_BUTTON_SCHEDULER
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_BUTTON_WORKOUT
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_LOGOUT_BUTTON
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_NOTIFICATIONS_BUTTON
import br.com.fitnesspro.ui.state.HomeUIState
import br.com.fitnesspro.ui.viewmodel.HomeViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAccountInformation: OnNavigateToAccountInformation,
    onNavigateToSchedule: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreen(
        state = state,
        onNavigateToAccountInformation = onNavigateToAccountInformation,
        onNavigateToSchedule = onNavigateToSchedule,
        onLogoutClick = viewModel::logout,
        onNavigateToLogin = onNavigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUIState,
    onNavigateToAccountInformation: OnNavigateToAccountInformation? = null,
    onNavigateToSchedule: () -> Unit = { },
    onLogoutClick: OnLogoutClick? = null,
    onNavigateToLogin: () -> Unit = { }
) {
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                actions = {
                    IconButtonNotification(
                        modifier = Modifier.testTag(HOME_SCREEN_NOTIFICATIONS_BUTTON.name)
                    )
                    IconButtonLogout(
                        modifier = Modifier.testTag(HOME_SCREEN_LOGOUT_BUTTON.name),
                        onClick = {
                            onLogoutClick?.onExecute {
                                onNavigateToLogin()
                            }
                        }
                    )
                },
                showNavigationIcon = true,
                customNavigationIcon = {
                    IconButtonAccount(
                        modifier = Modifier.testTag(HOME_SCREEN_ACCOUNT_BUTTON.name),
                        onClick = {
                            onNavigateToAccountInformation?.onNavigate(RegisterUserScreenArgs())
                        }
                    )
                },
                showMenuWithLogout = false,
            )
        }
    ) { padding ->
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val (footerRef, moduleButtonsRef) = createRefs()

            FitnessProMessageDialog(state = state.messageDialogState)

            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 32.dp, vertical = 48.dp)
                    .constrainAs(moduleButtonsRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(footerRef.top)

                        verticalBias = 1f
                    }

            ) {
                var openedBottomSheetWorkout by rememberSaveable { mutableStateOf(false) }

                val (btnSchedulerRef, btnWorkoutRef, btnNutritionRef, btnMoneyRef) = createRefs()

                createHorizontalChain(btnSchedulerRef, btnWorkoutRef)
                createHorizontalChain(btnNutritionRef, btnMoneyRef)

                createVerticalChain(btnSchedulerRef, btnNutritionRef)
                createVerticalChain(btnWorkoutRef, btnMoneyRef)

                SquaredButton(
                    modifier = Modifier
                        .testTag(HOME_SCREEN_BUTTON_SCHEDULER.name)
                        .padding(8.dp)
                        .constrainAs(btnSchedulerRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)

                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                            horizontalChainWeight = 0.5f
                            verticalChainWeight = 0.5f
                        },
                    iconResId = br.com.fitnesspro.core.R.drawable.ic_calendar_32dp,
                    label = stringResource(R.string.home_screen_label_btn_scheduler),
                    enabled = state.isEnabledSchedulerButton,
                    onClick = {
                        Firebase.analytics.logButtonClick(HOME_SCREEN_BUTTON_SCHEDULER)
                        onNavigateToSchedule()
                    }
                )

                SquaredButton(
                    modifier = Modifier
                        .testTag(HOME_SCREEN_BUTTON_WORKOUT.name)
                        .padding(8.dp)
                        .constrainAs(btnWorkoutRef) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)

                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                            horizontalChainWeight = 0.5f
                            verticalChainWeight = 0.5f
                        },
                    iconResId = br.com.fitnesspro.core.R.drawable.ic_dumbbell_32dp,
                    label = stringResource(R.string.home_screen_label_btn_workout),
                    enabled = state.isEnabledWorkoutButton,
                    onClick = {
                        Firebase.analytics.logButtonClick(HOME_SCREEN_BUTTON_WORKOUT)
                        openedBottomSheetWorkout = true
                    }
                )

                if (openedBottomSheetWorkout) {
                    BottomSheetWorkout(
                        userType = state.toPerson?.toUser?.type!!,
                        onDismissRequest = { openedBottomSheetWorkout = false },
                        onItemClickListener = {
                            openedBottomSheetWorkout = false
                        }
                    )
                }

                SquaredButton(
                    modifier = Modifier
                        .testTag(HOME_SCREEN_BUTTON_NUTRITION.name)
                        .padding(8.dp)
                        .constrainAs(btnNutritionRef) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)

                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                            horizontalChainWeight = 0.5f
                            verticalChainWeight = 0.5f
                        },
                    iconResId = br.com.fitnesspro.core.R.drawable.ic_flatware_32dp,
                    label = stringResource(R.string.home_screen_label_btn_nutrition),
                    enabled = state.isEnabledNutritionButton,
                    onClick = {
                        Firebase.analytics.logButtonClick(HOME_SCREEN_BUTTON_NUTRITION)
                    }
                )

                SquaredButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(btnMoneyRef) {
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)

                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                            horizontalChainWeight = 0.5f
                            verticalChainWeight = 0.5f
                        },
                    iconResId = br.com.fitnesspro.core.R.drawable.ic_money_32dp,
                    label = stringResource(R.string.home_screen_label_btn_money),
                    enabled = state.isEnabledMoneyButton,
                    onClick = {
                        Firebase.analytics.logButtonClick(HOME_SCREEN_BUTTON_MONEY)
                    }
                )
            }

            ConstraintLayout(
                Modifier
                    .constrainAs(footerRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                val (companyNameRef, appVersionRef) = createRefs()

                createHorizontalChain(companyNameRef, appVersionRef)

                Text(
                    modifier = Modifier.constrainAs(companyNameRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    text = stringResource(R.string.home_screen_company_name),
                    style = LabelTextStyle,
                    color = GREY_600,
                )

                Text(
                    modifier = Modifier.constrainAs(appVersionRef) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    text = stringResource(R.string.home_screen_app_version),
                    style = LabelTextStyle,
                    color = GREY_600,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    FitnessProTheme {
        Surface {
            HomeScreen(
                state = HomeUIState(
                    title = "Membro",
                    subtitle = "Nikolas Luiz Schmitt",
                    isEnabledSchedulerButton = true,
                    isEnabledWorkoutButton = true,
                    isEnabledNutritionButton = true,
                    isEnabledMoneyButton = true
                )
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenDisabledPreview() {
    FitnessProTheme {
        Surface {
            HomeScreen(
                state = HomeUIState(
                    title = "Membro",
                    subtitle = "Nikolas Luiz Schmitt"
                )
            )
        }
    }
}