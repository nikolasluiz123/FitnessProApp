package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.professional

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerDetailsTestTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertRequiredTextFieldValidation
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ProfessionalUniqueCompromiseScreenFieldsValidationUITests : BaseAuthenticatedUITest() {

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
    fun should_show_error_when_member_is_not_informed_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClick(COMPROMISE_SCREEN_FAB_SAVE)
                assertWithText(
                    activity.getString(
                        br.com.fitnesspro.common.R.string.validation_msg_required_field,
                        activity.getString(EnumValidatedCompromiseFields.MEMBER.labelResId)
                    ),
                    COMPROMISE_SCREEN_MEMBER_FIELD
                )
            }
        }
    }

    @Test
    fun should_show_error_when_start_time_is_not_informed_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClick(COMPROMISE_SCREEN_FAB_SAVE)
                assertWithText(
                    activity.getString(
                        br.com.fitnesspro.common.R.string.validation_msg_required_field,
                        activity.getString(EnumValidatedCompromiseFields.HOUR_START.labelResId)
                    ),
                    COMPROMISE_SCREEN_START_HOUR_FIELD
                )
            }
        }
    }

    @Test
    fun should_show_error_when_start_time_is_after_end_time_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                writeTextField(COMPROMISE_SCREEN_START_HOUR_FIELD, "1500")
                writeTextField(COMPROMISE_SCREEN_END_HOUR_FIELD, "1300")
                onClick(COMPROMISE_SCREEN_FAB_SAVE)
                assertWithText(
                    activity.getString(R.string.save_compromise_invalid_hour_period),
                    FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
                )
            }
        }
    }

    @Test
    fun should_show_error_when_end_time_is_not_informed_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertRequiredTextFieldValidation(
                    COMPROMISE_SCREEN_END_HOUR_FIELD,
                    COMPROMISE_SCREEN_FAB_SAVE,
                    activity.getString(
                        br.com.fitnesspro.common.R.string.validation_msg_required_field,
                        activity.getString(EnumValidatedCompromiseFields.HOUR_END.labelResId)
                    )
                )
            }

        }
    }


    private suspend fun executeDefaultPrepareForTest() {
        prepareDatabaseWithPersons()
        authenticatePersonal()
    }

    private fun AndroidComposeTestRule<*, *>.navigateToCompromise() {
        onClickWithText("21")
        onClick(SCHEDULER_DETAILS_SCREEN_FAB_ADD)
    }

    companion object {
         const val TAG = "ProfessionalUniqueCompromiseScreenFieldsValidationUITests"
    }
}