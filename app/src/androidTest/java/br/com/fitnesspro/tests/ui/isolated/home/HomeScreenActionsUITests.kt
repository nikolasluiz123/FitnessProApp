package br.com.fitnesspro.tests.ui.isolated.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.R
import br.com.fitnesspro.common.ui.enums.EnumBottomSheetsTestTags.BOTTOM_SHEET_WORKOUT
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_CONFIRM_BUTTON
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertDisplayedWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTags.HOME_SCREEN_BUTTON_SCHEDULER
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTags.HOME_SCREEN_BUTTON_WORKOUT
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTags.HOME_SCREEN_LOGOUT_BUTTON
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeScreenActionsUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag() = TAG

    override fun getStartingDestination() = homeScreenRoute

    override fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
        loginScreen(
            onNavigateToRegisterUser = navController::navigateToRegisterUserScreen,
            onNavigateToHome = {
                navController.navigateToHomeScreen(
                    navOptions = navOptions {
                        popUpTo(loginScreenRoute) { inclusive = true }
                    }
                )
            },
            onNavigateToMockScreen = navController::navigateToMockScreen
        )

        registerUserScreen(
            onBackClick = navController::popBackStack,
            onAddAcademyClick = navController::navigateToRegisterAcademyScreen,
            onAcademyItemClick = navController::navigateToRegisterAcademyScreen
        )

        homeScreen(
            onNavigateToAccountInformation = navController::navigateToRegisterUserScreen,
            onNavigateToSchedule = navController::navigateToScheduleScreen,
            onNavigateToLogin = {
                navController.navigateToLoginScreen(
                    navOptions = navOptions {
                        popUpTo(homeScreenRoute) { inclusive = true }
                    }
                )
            }
        )

        schedulerScreen(
            onBackClick = navController::popBackStack,
            onDayClick = navController::navigateToSchedulerDetailsScreen,
            onNavigateToCompromise = navController::navigateToCompromiseScreen,
            onNavigateToConfig = navController::navigateToSchedulerConfigScreen,
            onNavigateToChatHistory = navController::navigateToChatHistoryScreen
        )
    }

    @Test
    fun should_navigate_to_scheduler_screen_when_click_on_schedule_button() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_BUTTON_SCHEDULER)
            assertDisplayedWithText(activity.getString(br.com.fitnesspro.scheduler.R.string.schedule_screen_title))
        }
    }

    @Test
    fun should_show_bottom_sheet_when_member_click_on_workout_button() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_BUTTON_WORKOUT)
            assertDisplayed(BOTTOM_SHEET_WORKOUT)
            assertDisplayedWithText(activity.getString(R.string.label_evolution_bottom_sheet_workout))
            assertDisplayedWithText(activity.getString(R.string.label_workout_bottom_sheet_workout))
        }
    }

    @Test
    fun should_show_bottom_sheet_when_personal_click_on_workout_button() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_BUTTON_WORKOUT)
            assertDisplayed(BOTTOM_SHEET_WORKOUT)
            assertDisplayedWithText(activity.getString(R.string.label_follow_up_evolution_bottom_sheet_workout))
            assertDisplayedWithText(activity.getString(R.string.label_workout_setup_bottom_sheet_workout))
            assertDisplayedWithText(activity.getString(R.string.label_predefinitions_bottom_sheet_workout))
        }
    }

    @Test
    fun should_navigate_to_login_when_click_on_logout_button() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_LOGOUT_BUTTON)
            onClick(FITNESS_PRO_MESSAGE_DIALOG_CONFIRM_BUTTON)
            assertDisplayedWithText(activity.getString(br.com.fitnesspro.common.R.string.login_screen_title))
        }
    }

    companion object {
        private const val TAG = "HomeScreenActionsUITests"
    }
}