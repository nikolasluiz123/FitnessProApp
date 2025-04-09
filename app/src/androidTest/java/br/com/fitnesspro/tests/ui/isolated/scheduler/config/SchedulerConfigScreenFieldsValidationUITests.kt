package br.com.fitnesspro.tests.ui.isolated.scheduler.config

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MAX_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MIN_SCHEDULE_DENSITY
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertRequiredTextFieldValidation
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SchedulerConfigScreenFieldsValidationUITests : BaseAuthenticatedUITest() {

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
    fun should_show_error_when_min_density_is_empty_and_save_is_clicked() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            waitUntil(5000) {
                onNodeWithTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name).isDisplayed()
            }

            assertRequiredTextFieldValidation(
                fieldTag = SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD,
                buttonTag = SCHEDULER_CONFIG_SCREEN_FAB_SAVE,
                message = activity.getString(
                    R.string.validation_msg_required_field,
                    activity.getString(MIN_SCHEDULE_DENSITY.labelResId)
                )
            )
        }
    }

    @Test
    fun should_show_error_when_max_density_is_empty_and_save_is_clicked() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            waitUntil(5000) {
                onNodeWithTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name).isDisplayed()
            }

            assertRequiredTextFieldValidation(
                fieldTag = SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD,
                buttonTag = SCHEDULER_CONFIG_SCREEN_FAB_SAVE,
                message = activity.getString(
                    R.string.validation_msg_required_field,
                    activity.getString(MAX_SCHEDULE_DENSITY.labelResId)
                )
            )
        }
    }

    private suspend fun defaultPersonalPreset() {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        createSchedulerConfigPersonal()
        setNavHostContent()
    }

    private suspend fun createSchedulerConfigPersonal() {
        val personId = toPersons[0].id!!

        val result = schedulerConfigUseCase.saveConfig(personId)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating scheduler config: ${result.first().message}")
        }
    }

    companion object {
        const val TAG = "SchedulerConfigScreenFieldsValidationUITests"
    }
}