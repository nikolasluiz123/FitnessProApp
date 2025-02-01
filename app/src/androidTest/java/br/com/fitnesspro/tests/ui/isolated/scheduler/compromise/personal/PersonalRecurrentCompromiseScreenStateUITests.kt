package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.personal

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_TITLE
import br.com.fitnesspro.scheduler.R.string
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTestTags.SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PersonalRecurrentCompromiseScreenStateUITests : BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    @Inject
    lateinit var saveCompromiseUseCase: SaveCompromiseUseCase

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

        compromiseScreen(
            onBackClick = navController::popBackStack
        )
    }

    @Test
    fun should_show_title_when_is_new_recurrent_compromise() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertWithText(
                    activity.getString(string.compromise_screen_title_new_recurrent_compromise),
                    FITNESS_PRO_TOP_APP_BAR_TITLE
                )
            }
        }

    }

    private fun AndroidComposeTestRule<*, *>.waitRecurrentScheduleFABVisibleAndClick() {
        waitUntil(2000) {
            onNodeWithTag(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB.name).isDisplayed()
        }

        onClick(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB)
    }

    companion object {
        const val TAG = "PersonalRecurrentCompromiseScreenStateUITests"
    }
}