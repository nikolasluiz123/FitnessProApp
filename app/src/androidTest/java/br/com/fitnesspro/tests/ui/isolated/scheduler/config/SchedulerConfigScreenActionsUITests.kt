package br.com.fitnesspro.tests.ui.isolated.scheduler.config

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_FAB_SAVE
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayedWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SchedulerConfigScreenActionsUITests : BaseAuthenticatedUITest() {

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
    fun should_show_success_message_when_all_fields_are_valid_and_save_is_clicked() = runTest {
        defaultPersonalPreset()

        composeTestRule.apply {
            onClick(SCHEDULER_CONFIG_SCREEN_FAB_SAVE)

            waitUntil {
                onNodeWithText(activity.getString(R.string.scheduler_config_screen_message_success))
                    .isDisplayed()
            }

            assertDisplayedWithText(activity.getString(R.string.scheduler_config_screen_message_success))
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
        const val TAG = "SchedulerConfigScreenActionsUITests"
    }
}