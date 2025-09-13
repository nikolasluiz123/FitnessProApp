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
import br.com.fitnesspro.workout.ui.navigation.currentWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.dayWeekExercisesScreen
import br.com.fitnesspro.workout.ui.navigation.dayWeekWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.executionBarChartScreen
import br.com.fitnesspro.workout.ui.navigation.executionEvolutionHistoryScreen
import br.com.fitnesspro.workout.ui.navigation.exerciseDetailsScreen
import br.com.fitnesspro.workout.ui.navigation.exercisesScreen
import br.com.fitnesspro.workout.ui.navigation.membersEvolutionScreen
import br.com.fitnesspro.workout.ui.navigation.membersWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToCurrentWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToDayWeekExercisesScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToDayWeekWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToExecutionBarChartScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToExecutionEvolutionHistoryScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToExercisesScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToMembersEvolutionScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToMembersWorkoutScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToPreDefinitionScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToPreDefinitionsScreen
import br.com.fitnesspro.workout.ui.navigation.navigateToRegisterEvolutionScreen
import br.com.fitnesspro.workout.ui.navigation.preDefinitionScreen
import br.com.fitnesspro.workout.ui.navigation.preDefinitionsScreen
import br.com.fitnesspro.workout.ui.navigation.registerEvolutionScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
            onNavigateToCurrentWorkoutScreen = navController::navigateToCurrentWorkoutScreen,
            onNavigateToPreDefinitionsScreen = navController::navigateToPreDefinitionsScreen,
            onNavigateToMemberEvolutionScreen = navController::navigateToMembersEvolutionScreen,
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
            onBackClick = navController::popBackStack,
            onNavigateToExerciseDetails = navController::navigateToExerciseDetailsScreen
        )

        dayWeekExercisesScreen(
            onBackClick = navController::popBackStack,
            onNavigateExercise = navController::navigateToExercisesScreen
        )

        exercisesScreen(
            onBackClick = navController::popBackStack
        )

        currentWorkoutScreen(
            onBackClick = navController::popBackStack,
            onNavigateToDayWeekWorkout = navController::navigateToDayWeekWorkoutScreen
        )

        exerciseDetailsScreen(
            onBackClick = navController::popBackStack,
            onNavigateToRegisterEvolution = navController::navigateToRegisterEvolutionScreen
        )

        registerEvolutionScreen(
            onBackClick = navController::popBackStack
        )

        preDefinitionsScreen(
            onBackClick = navController::popBackStack,
            onNavigateToPreDefinition = navController::navigateToPreDefinitionScreen
        )

        preDefinitionScreen(
            onBackClick = navController::popBackStack
        )

        generatedReportsScreen(
            onNavigateBackClick = navController::popBackStack
        )

        membersEvolutionScreen(
            onBackClick = navController::popBackStack,
            onNavigateToExecutionEvolutionHistory = navController::navigateToExecutionEvolutionHistoryScreen
        )

        executionEvolutionHistoryScreen(
            onBackClick = navController::popBackStack,
            onHistoryClick = {
                navController.navigateToExecutionBarChartScreen()
            }
        )

        executionBarChartScreen()
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