package br.com.fitnesspro.tests.ui.isolated.scheduler.details

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SchedulerDetailsStateScreenUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag(): String = TAG

    override fun getStartingDestination(): String = schedulerScreenRoute

    override fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
        schedulerScreen(
            onBackClick = navController::popBackStack,
            onDayClick = navController::navigateToSchedulerDetailsScreen,
            onNavigateToCompromise = navController::navigateToCompromiseScreen,
            onNavigateToConfig = navController::navigateToSchedulerConfigScreen
        )

        schedulerDetailsScreen(
            onBackClick = navController::popBackStack,
            onNavigateToCompromise = navController::navigateToCompromiseScreen
        )
    }

    @Test
    fun should_navigate_to_read_only_details_screen_when_day_clicked_is_past() {

    }

    @Test
    fun should_show_empty_message_when_click_on_day_without_schedules() {

    }

    @Test
    fun should_format_hour_when_list_items() {

    }

    @Test
    fun should_show_date_of_clicked_day_in_subtitle_when_navigate_to_details_screen() {

    }

    @Test
    fun should_show_member_name_in_item_when_list_items() {

    }

    @Test
    fun should_show_compromise_type_when_type_is_recurrent() {

    }

    @Test
    fun should_show_compromise_type_when_type_is_first() {

    }

    @Test
    fun should_show_situation_when_situation_is_scheduled() {

    }

    @Test
    fun should_show_situation_when_situation_is_confirmed() {

    }

    @Test
    fun should_show_situation_when_situation_is_canceled() {

    }

    @Test
    fun should_show_situation_when_situation_is_completed() {

    }

    companion object {
        private const val TAG = "SchedulerDetailsStateScreenUITests"
    }
}