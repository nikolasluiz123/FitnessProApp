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
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerAcademyScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_ACTION_BUTTON_DELETE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_DIALOG_ACADEMIES_LIST_ITEM
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FAB_SAVE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_END
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_START
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_ADD
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_ACADEMY_LIST_ITEM
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.compose.components.fields.enums.EnumDropdownMenuTestTags.DROP_DOWN_MENU_ITEM
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.list.grouped.expandable.enums.EnumLazyExpandableListTestTags.EXPANDABLE_LIST_ITEM
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_SUBTITLE
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_TITLE
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertEnabled
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onPosition
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
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
class RegisterAcademyScreenStateUITests: BaseAuthenticatedUITest() {

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
    fun should_show_title_when_member_is_authenticated() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            assertWithText(
                activity.getString(R.string.register_academy_title_new_student),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_title_when_professional_is_authenticated() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            assertWithText(
                activity.getString(R.string.register_academy_title_new_professional),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_title_with_hour_range_when_is_edition() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        createAcademyTimePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            onPosition(1, TAB).performClick()
            onPosition(0, EXPANDABLE_LIST_ITEM).performClick()
            onPosition(0, REGISTER_USER_SCREEN_TAB_ACADEMY_LIST_ITEM).performClick()
            assertWithText(
                activity.getString(
                    R.string.register_academy_screen_title_edit,
                    DayOfWeek.MONDAY.getFirstPartFullDisplayName(),
                    "09:00",
                    "10:00"
                ),
                FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_subtitle_with_academy_name_when_is_edition() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        createAcademyTimePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            onPosition(1, TAB).performClick()
            onPosition(0, EXPANDABLE_LIST_ITEM).performClick()
            onPosition(0, REGISTER_USER_SCREEN_TAB_ACADEMY_LIST_ITEM).performClick()
            assertWithText("academy", FITNESS_PRO_TOP_APP_BAR_SUBTITLE)
        }
    }

    @Test
    fun should_enable_inactivate_button_when_is_edition() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        createAcademyTimePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onClick(HOME_SCREEN_ACCOUNT_BUTTON)
            onPosition(1, TAB).performClick()
            onPosition(0, EXPANDABLE_LIST_ITEM).performClick()
            onPosition(0, REGISTER_USER_SCREEN_TAB_ACADEMY_LIST_ITEM).performClick()
            assertEnabled(REGISTER_ACADEMY_ACTION_BUTTON_DELETE, true)
        }
    }

    @Test
    fun should_enable_inactivate_button_when_all_fields_valid_and_save() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        createAcademy()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()

            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY)
            onPosition(0, REGISTER_ACADEMY_DIALOG_ACADEMIES_LIST_ITEM).performClick()

            onPosition(1, OUTLINED_TEXT_FIELD_TRAILING_ICON).performClick()
            onPosition(0, DROP_DOWN_MENU_ITEM).performClick()

            writeTextField(REGISTER_ACADEMY_SCREEN_FIELD_START, "0530")
            writeTextField(REGISTER_ACADEMY_SCREEN_FIELD_END, "0630")

            onClick(REGISTER_ACADEMY_SCREEN_FAB_SAVE)

            waitUntil {
                onNodeWithText(
                    activity.getString(R.string.register_academy_screen_success_save_message)
                ).isDisplayed()
            }

            assertEnabled(REGISTER_ACADEMY_ACTION_BUTTON_DELETE, true)
        }
    }

    @Test
    fun should_disable_inactivate_button_when_is_creation() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            assertEnabled(REGISTER_ACADEMY_ACTION_BUTTON_DELETE, false)
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
        private const val TAG = "RegisterAcademyScreenStateUITests"
    }
}