package br.com.fitnesspro.tests.ui.isolated.login

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.enums.EnumBottomSheetsTestTags.BOTTOM_SHEET_REGISTER_USER
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_EMAIL_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_LOGIN_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_PASSWORD_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_REGISTER_BUTTON
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_TITLE
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertDisplayedWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginScreenActionsUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag() = TAG

    override fun getStartingDestination() = loginScreenRoute

    override fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
        loginScreen(
            onBottomSheetRegisterUserItemClick = navController::navigateToRegisterUserScreen,
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
    }

    @Test
    fun should_show_bottom_sheet_when_click_on_register_button() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            assertDisplayed(BOTTOM_SHEET_REGISTER_USER)
            assertDisplayedWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            assertDisplayedWithText(activity.getString(R.string.label_trainer_bottom_sheet_register_user))
            assertDisplayedWithText(activity.getString(R.string.label_nutritionist_bottom_sheet_register_user))
        }
    }


    @Test
    fun should_navigate_to_home_when_login_success() = runTest {
        prepareDatabaseWithPersons()
        setNavHostContent()

        composeTestRule.apply {
            writeTextField(LOGIN_SCREEN_EMAIL_FIELD, PERSONAL_EMAIL)
            writeTextField(LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(LOGIN_SCREEN_LOGIN_BUTTON)

            waitUntil(2000) {
                onNodeWithTag(FITNESS_PRO_TOP_APP_BAR_TITLE.name)
                    .assert(hasText(activity.getString(br.com.fitnesspro.R.string.home_screen_title_personal_trainer)))
                    .isDisplayed()
            }
        }
    }

    companion object {
        private const val TAG = "LoginScreenActionsUITests"
    }
}