package br.com.fitnesspro.tests.ui.isolated.register.academy

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import androidx.test.ext.junit.rules.ActivityScenarioRule
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerAcademyScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_ACTION_BUTTON_DELETE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_ADD
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_ACADEMY_LIST_ITEM
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
import br.com.fitnesspro.compose.components.list.grouped.expandable.enums.EnumLazyExpandableListTestTags.EXPANDABLE_LIST_ITEM
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_NAVIGATE_BACK_BUTTON
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_TITLE
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onPosition
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_ACCOUNT_BUTTON
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

@HiltAndroidTest
class RegisterAcademyScreenActionsUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    @Inject
    lateinit var saveAcademyTimeUseCase: SavePersonAcademyTimeUseCase

    @Inject
    lateinit var academyDAO: AcademyDAO

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag() = TAG

    override fun getStartingDestination() = homeScreenRoute

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
    fun should_show_confirmation_message_when_click_on_inactivate_button() = runTest {
        prepareDatabaseWithPersons()
        createAcademyTimePersonal()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            onPosition(1, TAB).performClick()
            onClickFirst(EXPANDABLE_LIST_ITEM)
            onClickFirst(REGISTER_USER_SCREEN_TAB_ACADEMY_LIST_ITEM)
            onClick(REGISTER_ACADEMY_ACTION_BUTTON_DELETE)
            assertWithText(
                activity.getString(R.string.register_academy_screen_message_inactivate_academy),
                FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
            )
        }
    }

    @Test
    fun should_navigate_to_home_user_when_click_on_back_button() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            onClick(FITNESS_PRO_TOP_APP_BAR_NAVIGATE_BACK_BUTTON)
            assertWithText(
                activity.getString(br.com.fitnesspro.R.string.home_screen_title_academy_member),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_success_message_when_save_with_all_fields_valid() = runTest {
        prepareDatabaseWithPersons()
        createAcademyTimePersonal()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            onPosition(1, TAB).performClick()
            onClickFirst(EXPANDABLE_LIST_ITEM)
            onClickFirst(REGISTER_USER_SCREEN_TAB_ACADEMY_LIST_ITEM)
            onClick(EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FAB_SAVE)

            waitUntil(2000) {
                onNodeWithText(
                    activity.getString(R.string.register_academy_screen_success_save_message),
                ).isDisplayed()
            }
        }
    }

    private suspend fun createAcademyTimePersonal() {
        val academy = createAcademy()

        val toPersonAcademyTime = TOPersonAcademyTime(
            personId = toPersons[0].id!!,
            toAcademy = TOAcademy(id = academy.id, name = academy.name),
            timeStart = LocalTime.of(9, 0),
            timeEnd = LocalTime.of(10, 0),
            dayOfWeek = DayOfWeek.MONDAY
        )

        val result = saveAcademyTimeUseCase.execute(toPersonAcademyTime)

        if (result.isNotEmpty()) {
            Log.e(TAG, "createAcademyTimePersonal Error ${result.map { it.validationType }}")
        }
    }

    private suspend fun createAcademy(): Academy {
        val academy = Academy(id = "1", name = "academy")
        academyDAO.saveAcademiesBatch(listOf(academy))

        return academy
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<AndroidTestsActivity>, AndroidTestsActivity>.navigateToRegisterAcademy() {
        onClick(HOME_SCREEN_ACCOUNT_BUTTON)
        onPosition(1, TAB).performClick()
        onClick(REGISTER_USER_SCREEN_FAB_ADD)
    }

    companion object {
        private const val TAG = "RegisterAcademyScreenActionsUITests"
    }
}