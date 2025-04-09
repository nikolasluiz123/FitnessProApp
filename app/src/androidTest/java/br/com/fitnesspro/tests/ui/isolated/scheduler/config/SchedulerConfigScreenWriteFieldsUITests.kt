package br.com.fitnesspro.tests.ui.isolated.scheduler.config

import android.util.Log
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.compose.components.buttons.enums.EnumSwitchButtonTestTags.HORIZONTAL_LABELED_SWITCH_BUTTON_LABEL
import br.com.fitnesspro.compose.components.buttons.enums.EnumSwitchButtonTestTags.SWITCH_BUTTON
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertOnlyDigitsFieldValidation
import br.com.fitnesspro.tests.ui.extensions.assertPositionWithText
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onPosition
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SchedulerConfigScreenWriteFieldsUITests : BaseAuthenticatedUITest() {

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
    fun should_change_checked_of_alarm_switch_when_is_clicked_and_is_unchecked() = runTest {
        defaultMemberPreset()

        composeTestRule.apply {
            assertPositionWithText(
                text = activity.getString(R.string.scheduler_config_screen_label_alarm),
                position = 0,
                testTag = HORIZONTAL_LABELED_SWITCH_BUTTON_LABEL
            )
            onPosition(0, SWITCH_BUTTON).assertIsOff()
            onPosition(0, SWITCH_BUTTON).performClick().assertIsOn()
            onPosition(0, SWITCH_BUTTON).performClick().assertIsOff()
        }
    }

    @Test
    fun should_change_checked_of_notification_switch_when_is_clicked_and_is_unchecked() = runTest {
        defaultMemberPreset()

        composeTestRule.apply {
            assertPositionWithText(
                text = activity.getString(R.string.scheduler_config_screen_label_notification),
                position = 1,
                testTag = HORIZONTAL_LABELED_SWITCH_BUTTON_LABEL
            )
            onPosition(1, SWITCH_BUTTON).assertIsOff()
            onPosition(1, SWITCH_BUTTON).performClick().assertIsOn()
            onPosition(1, SWITCH_BUTTON).performClick().assertIsOff()
        }
    }

    @Test
    fun should_show_text_on_min_density_field_when_write() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            assertWriteTextField("10", SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD)
        }
    }

    @Test
    fun should_show_text_on_max_density_field_when_write() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            assertWriteTextField("15", SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD)
        }
    }

    @Test
    fun should_permit_only_digits_on_min_density_field_when_write_in_input() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            assertOnlyDigitsFieldValidation(SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD, "aaaa")
        }
    }

    @Test
    fun should_permit_only_digits_on_max_density_field_when_write_in_input() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            assertOnlyDigitsFieldValidation(SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD, "aaaa")
        }
    }

    private suspend fun defaultPersonalPreset() {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        createSchedulerConfigPersonal()
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

    private suspend fun createSchedulerConfigMember() {
        val personId = toPersons[2].id!!

        val result = schedulerConfigUseCase.saveConfig(personId)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating scheduler config: ${result.first().message}")
        }
    }

    companion object {
        const val TAG = "SchedulerConfigScreenWriteFieldsUITests"
    }
}