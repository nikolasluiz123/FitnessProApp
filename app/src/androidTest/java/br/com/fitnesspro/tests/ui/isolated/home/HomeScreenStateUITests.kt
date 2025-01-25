package br.com.fitnesspro.tests.ui.isolated.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.R
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeScreenStateUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

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

        schedulerScreen(
            onBackClick = navController::popBackStack,
            onDayClick = navController::navigateToSchedulerDetailsScreen,
            onNavigateToCompromise = navController::navigateToCompromiseScreen,
            onNavigateToConfig = navController::navigateToSchedulerConfigScreen
        )
    }

    @Test
    fun should_show_member_label_in_title_when_member_is_logged() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            onNodeWithText(activity.getString(R.string.home_screen_title_academy_member))
                .assertIsDisplayed()
        }
    }

    @Test
    fun should_show_trainer_label_in_title_when_trainer_is_logged() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onNodeWithText(activity.getString(R.string.home_screen_title_personal_trainer))
                .assertIsDisplayed()
        }
    }

    @Test
    fun should_show_nutritionist_label_in_title_when_nutritionist_is_logged() = runTest {
        prepareDatabaseWithPersons()
        authenticateNutritionist()
        setNavHostContent()

        composeTestRule.apply {
            onNodeWithText(activity.getString(R.string.home_screen_title_nutritionist))
                .assertIsDisplayed()
        }
    }

    @Test
    fun should_show_person_name_in_subtitle_when_any_user_is_logged() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            onNodeWithText(toPersons[0].name!!)
                .assertIsDisplayed()
        }
    }

    companion object {
        private const val TAG = "HomeScreenStateUITests"
    }
}