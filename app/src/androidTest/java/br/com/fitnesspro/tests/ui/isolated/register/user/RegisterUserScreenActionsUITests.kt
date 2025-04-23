package br.com.fitnesspro.tests.ui.isolated.register.user

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R.string
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerAcademyScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_EMAIL_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_PASSWORD_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_REGISTER_BUTTON
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_SAVE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_ACADEMY
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_BIRTH_DATE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD
import br.com.fitnesspro.compose.components.dialog.enums.EnumDatePickerDialogTestTags.DATE_PICKER_DIALOG
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_NAVIGATE_BACK_BUTTON
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.onPosition
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTags.HOME_SCREEN_ACCOUNT_BUTTON
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class RegisterUserScreenActionsUITests: BaseAuthenticatedUITest() {

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

        registerAcademyScreen(
            onBackClick = navController::popBackStack,
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
    fun should_show_success_message_when_save_and_all_fields_are_valid() = runTest {
        prepareDatabaseWithPersons()
        setNavHostContent()

        composeTestRule.apply {
            writeTextField(LOGIN_SCREEN_EMAIL_FIELD, PERSONAL_EMAIL)
            writeTextField(LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(LOGIN_SCREEN_LOGIN_BUTTON)
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            writeTextField(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD, DEFAULT_PASSWORD)
            onClick(REGISTER_USER_SCREEN_FAB_SAVE)

            waitUntil {
                onNodeWithText(activity.getString(string.register_user_screen_success_message))
                    .isDisplayed()
            }
        }
    }

    @Test
    fun should_change_to_academy_list_when_click_on_tab() = runTest {
        prepareDatabaseWithPersons()
        setNavHostContent()

        composeTestRule.apply {
            writeTextField(LOGIN_SCREEN_EMAIL_FIELD, PERSONAL_EMAIL)
            writeTextField(LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(LOGIN_SCREEN_LOGIN_BUTTON)
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            onPosition(1, TAB).performClick()
            assertDisplayed(REGISTER_USER_SCREEN_TAB_ACADEMY)
        }

    }

    @Test
    fun should_show_date_picker_when_click_on_birth_date_field() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(string.label_member_bottom_sheet_register_user))
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_BIRTH_DATE)
            assertDisplayed(DATE_PICKER_DIALOG)
        }
    }

    @Test
    fun should_navigate_login_when_click_on_back_button() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(string.label_member_bottom_sheet_register_user))
            onClick(FITNESS_PRO_TOP_APP_BAR_NAVIGATE_BACK_BUTTON)
            assertDisplayed(LOGIN_SCREEN_LOGIN_BUTTON)
        }
    }

    companion object {
        private const val TAG = "RegisterUserScreenActionsUITests"
    }
}