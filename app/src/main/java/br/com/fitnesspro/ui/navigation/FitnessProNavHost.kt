package br.com.fitnesspro.ui.navigation

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import br.com.fitnesspro.common.ui.navigation.generatedReportsScreen
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToGeneratedReportsScreen
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerAcademyScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.scheduler.ui.navigation.chatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.chatScreen
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.workout.ui.navigation.dayWeekExercisesScreen
import br.com.fitnesspro.workout.ui.navigation.dayWeekWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.exercisesScreen
import br.com.fitnesspro.workout.ui.navigation.membersWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToDayWeekExercisesScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToExercisesScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToMembersWorkoutScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun FitnessProNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = getStartDestination(),
        modifier = modifier
    ) {

        splashScreen(
            onNavigateToLogin = navController::navigateToLoginScreen,
            onNavigateToHome = navController::navigateToHomeScreen
        )

        loginScreen(
            onNavigateToRegisterUser = navController::navigateToRegisterUserScreen,
            onNavigateToHome = {
                navController.navigateToHomeScreen(
                    navOptions = navOptions {
                        popUpTo(loginScreenRoute) { inclusive = true }
                    }
                )
            },
        )

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
            onNavigateToMembersWorkoutScreen = navController::navigateToMembersWorkoutScreen,
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
            onNavigateToConfig = navController::navigateToSchedulerConfigScreen,
            onNavigateToChatHistory = navController::navigateToChatHistoryScreen,
            onNavigateToReports = navController::navigateToGeneratedReportsScreen
        )

        schedulerDetailsScreen(
            onBackClick = navController::popBackStack,
            onNavigateToCompromise = navController::navigateToCompromiseScreen
        )

        compromiseScreen(
            onBackClick = navController::popBackStack,
            onNavigateToChat = navController::navigateToChatScreen
        )

        schedulerConfigScreen(
            onBackClick = navController::popBackStack
        )

        chatHistoryScreen(
            onBackClick = navController::popBackStack,
            onNavigateToChat = navController::navigateToChatScreen
        )

        chatScreen(
            onBackClick = navController::popBackStack
        )

        membersWorkoutScreen(
            onBackClick = navController::popBackStack,
            onNavigateDayWeekExercises = navController::navigateToDayWeekExercisesScreen
        )

        dayWeekWorkoutScreen(
            onBackClick = navController::popBackStack
        )

        dayWeekExercisesScreen(
            onBackClick = navController::popBackStack,
            onNavigateExercise = navController::navigateToExercisesScreen
        )

        exercisesScreen(
            onBackClick = navController::popBackStack
        )

        generatedReportsScreen(
            onNavigateBackClick = navController::popBackStack
        )
    }
}

@Composable
private fun getStartDestination(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (Firebase.auth.currentUser != null) {
            homeScreenRoute
        } else {
            loginScreenRoute
        }
    } else {
        splashScreenRoute
    }
}