package br.com.fitnesspro.tests.ui.isolated.scheduler.config

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_ALARM
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertNotDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SchedulerConfigScreenStateUITests : BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    @Inject
    lateinit var schedulerConfigUseCase: SaveSchedulerConfigUseCase

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag(): String = TAG

    override fun getStartingDestination(): String = schedulerConfigScreenRoute

    override fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
        schedulerConfigScreen(
            onBackClick = navController::popBackStack
        )
    }

    @Test
    fun should_show_only_general_session_config_when_academy_member_navigate_to_config() = runTest {
        defaultMemberPreset()

        composeTestRule.apply {
            waitUntil(5000) {
                onNodeWithTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name).isDisplayed()
            }

            assertIsDisplayedGeneralSession()
            assertNotIsDisplayedDensityEventsSession()
            assertNotIsDisplayedWorkTimeSession()
            assertNotIsDisplayedBreakTimeSession()
        }
    }

    @Test
    fun should_show_all_sessions_config_when_personal_trainer_navigate_to_config() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            waitUntil(5000) {
                onNodeWithTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name).isDisplayed()
            }

            assertIsDisplayedGeneralSession()
            assertIsDisplayedDensityEventsSession()
            assertIsDisplayedWorkTimeSession()
            assertIsDisplayedBreakTimeSession()
        }
    }

    @Test
    fun should_show_all_sessions_config_when_nutritionist_navigate_to_config() = runTest {
        defaultNutritionistPreset()

        composeTestRule.apply {
            waitUntil(5000) {
                onNodeWithTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name).isDisplayed()
            }

            assertIsDisplayedGeneralSession()
            assertIsDisplayedDensityEventsSession()
            assertIsDisplayedWorkTimeSession()
            assertIsDisplayedBreakTimeSession()
        }
    }


    private fun AndroidComposeTestRule<*, *>.assertNotIsDisplayedBreakTimeSession() {
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME)
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME_EXPLANATION)
    }

    private fun AndroidComposeTestRule<*, *>.assertNotIsDisplayedWorkTimeSession() {
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME)
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME_EXPLANATION)
    }

    private fun AndroidComposeTestRule<*, *>.assertNotIsDisplayedDensityEventsSession() {
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS)
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS_EXPLANATION)
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD)
        assertNotDisplayed(SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD)
    }

    private fun AndroidComposeTestRule<*, *>.assertIsDisplayedBreakTimeSession() {
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME)
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME_EXPLANATION)
    }

    private fun AndroidComposeTestRule<*, *>.assertIsDisplayedWorkTimeSession() {
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME)
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME_EXPLANATION)
    }

    private fun AndroidComposeTestRule<*, *>.assertIsDisplayedDensityEventsSession() {
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS)
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS_EXPLANATION)
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD)
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD)
    }

    private fun AndroidComposeTestRule<*, *>.assertIsDisplayedGeneralSession() {
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL)
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_ALARM)
        assertDisplayed(SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION)
    }

    private suspend fun defaultPersonalPreset() {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        createSchedulerConfigPersonal()
        setNavHostContent()
    }

    private suspend fun defaultNutritionistPreset() {
        prepareDatabaseWithPersons()
        authenticateNutritionist()
        createSchedulerConfigNutritionist()
        setNavHostContent()
    }


    private suspend fun defaultMemberPreset() {
        prepareDatabaseWithPersons()
        authenticateMember()
        createSchedulerConfigMember()
        setNavHostContent()
    }

    private suspend fun createSchedulerConfigPersonal() {
        val personId = toPersons[0].id!!

        val result = schedulerConfigUseCase.saveConfig(personId)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating scheduler config: ${result.first().message}")
        }
    }

    private suspend fun createSchedulerConfigNutritionist() {
        val personId = toPersons[1].id!!

        val result = schedulerConfigUseCase.saveConfig(personId)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating scheduler config: ${result.first().message}")
        }
    }

    private suspend fun createSchedulerConfigMember() {
        val personId = toPersons[2].id!!

        val result = schedulerConfigUseCase.saveConfig(personId)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating scheduler config: ${result.first().message}")
        }
    }

    companion object {
        const val TAG = "SchedulerConfigScreenStateUITests"
    }
}