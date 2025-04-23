package br.com.fitnesspro.tests.ui.isolated.register.user

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_REGISTER_BUTTON
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_SAVE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PHONE
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_SUBTITLE
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_TITLE
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertNotDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
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
class RegisterUserScreenStateUITests: BaseAuthenticatedUITest() {

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
    fun should_show_new_member_title_when_click_member_on_bottom_sheet() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            assertWithText(
                activity.getString(R.string.register_user_screen_title_new_academy_member),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_member_title_when_click_account_information() = runTest {
        setNavHostContent()
        prepareDatabaseWithPersons()

        composeTestRule.apply {
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_EMAIL_FIELD, MEMBER_EMAIL)
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON)

            waitUntil {
                onNodeWithTag(HOME_SCREEN_ACCOUNT_BUTTON.name).isDisplayed()
            }

            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            assertWithText(
                activity.getString(R.string.register_user_screen_title_academy_member),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_new_trainer_title_when_click_personal_on_bottom_sheet() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_trainer_bottom_sheet_register_user))
            assertWithText(
                activity.getString(R.string.register_user_screen_title_new_personal_trainer),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_trainer_title_when_click_account_information() = runTest {
        setNavHostContent()
        prepareDatabaseWithPersons()

        composeTestRule.apply {
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_EMAIL_FIELD, PERSONAL_EMAIL)
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON)

            waitUntil {
                onNodeWithTag(HOME_SCREEN_ACCOUNT_BUTTON.name).isDisplayed()
            }

            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            assertWithText(
                activity.getString(R.string.register_user_screen_title_personal_trainer),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_new_nutritionist_title_when_click_nutritionist_on_bottom_sheet() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_nutritionist_bottom_sheet_register_user))
            assertWithText(
                activity.getString(R.string.register_user_screen_title_new_nutritionist),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_nutritionist_title_when_click_account_information() = runTest {
        setNavHostContent()
        prepareDatabaseWithPersons()

        composeTestRule.apply {
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_EMAIL_FIELD, NUTRITIONIST_EMAIL)
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON)

            waitUntil {
                onNodeWithTag(HOME_SCREEN_ACCOUNT_BUTTON.name).isDisplayed()
            }

            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            assertWithText(
                activity.getString(R.string.register_user_screen_title_nutritionist),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_subtitle_when_click_account_information() = runTest {
        setNavHostContent()
        prepareDatabaseWithPersons()

        composeTestRule.apply {
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_EMAIL_FIELD, NUTRITIONIST_EMAIL)
            writeTextField(EnumLoginScreenTags.LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON)

            waitUntil {
                onNodeWithTag(HOME_SCREEN_ACCOUNT_BUTTON.name).isDisplayed()
            }

            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            assertWithText(
                toPersons[1].name!!,
                FITNESS_PRO_TOP_APP_BAR_SUBTITLE
            )
        }
    }

    @Test
    fun should_disable_academies_tab_when_is_new_user() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            onPosition(0, TAB).assertIsEnabled()
            onPosition(1, TAB).assertIsNotEnabled()
        }
    }

    @Test
    fun should_enable_academies_tab_when_user_register_successfully() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            writeTextField(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME, "teste")
            writeTextField(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL, "teste@gmail.com")
            writeTextField(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD, "teste123456")
            onClick(REGISTER_USER_SCREEN_FAB_SAVE)

            waitUntil(2000) {
                onNodeWithText(activity.getString(R.string.register_user_screen_success_message))
                    .isDisplayed()
            }

            onPosition(1, TAB).assertIsEnabled()
        }
    }

    @Test
    fun should_show_phone_field_when_is_professional_creation() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_trainer_bottom_sheet_register_user))
            assertDisplayed(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PHONE)
        }
    }

    @Test
    fun should_show_phone_field_when_is_member_creation() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            assertNotDisplayed(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PHONE)
        }
    }

    companion object {
        private const val TAG = "RegisterUserScreenStateUITests"
    }
}