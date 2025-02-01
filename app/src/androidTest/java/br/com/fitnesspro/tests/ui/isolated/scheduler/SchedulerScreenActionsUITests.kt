package br.com.fitnesspro.tests.ui.isolated.scheduler

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.MONTH_YEAR
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.yearMonthNow
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTestTags.SCHEDULER_SCREEN_BACK_MONTH_BUTTON
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTestTags.SCHEDULER_SCREEN_FORWARD_MONTH_BUTTON
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTestTags.SCHEDULER_SCREEN_LABEL_YEAR_MONTH
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayedWithText
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SchedulerScreenActionsUITests: BaseAuthenticatedUITest() {

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
            onNavigateToConfig = navController::navigateToSchedulerConfigScreen,
            onNavigateToChatHistory = navController::navigateToChatHistoryScreen
        )

        schedulerDetailsScreen(
            onBackClick = navController::popBackStack,
            onNavigateToCompromise = navController::navigateToCompromiseScreen
        )
    }

    @Test
    fun should_go_to_next_month_when_click_forward_button() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            onClick(SCHEDULER_SCREEN_FORWARD_MONTH_BUTTON)
            assertWithText(
                yearMonthNow().plusMonths(1).format(MONTH_YEAR),
                SCHEDULER_SCREEN_LABEL_YEAR_MONTH
            )
        }
    }

    @Test
    fun should_go_to_previous_month_when_click_backward_button() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            onClick(SCHEDULER_SCREEN_BACK_MONTH_BUTTON)
            assertWithText(
                yearMonthNow().minusMonths(1).format(MONTH_YEAR),
                SCHEDULER_SCREEN_LABEL_YEAR_MONTH
            )
        }
    }

    @Test
    fun should_navigate_to_details_when_day_click() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            onClickWithText("20")
            assertDisplayedWithText(activity.getString(br.com.fitnesspro.scheduler.R.string.scheduler_details_screen_title))
        }
    }

    companion object {
        private const val TAG = "SchedulerScreenActionsUITests"
    }
}