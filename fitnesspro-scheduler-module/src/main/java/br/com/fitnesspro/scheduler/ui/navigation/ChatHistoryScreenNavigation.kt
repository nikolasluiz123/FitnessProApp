package br.com.fitnesspro.scheduler.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.scheduler.ui.screen.scheduler.ChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnNavigateToChat
import br.com.fitnesspro.scheduler.ui.viewmodel.ChatHistoryViewModel


internal const val chatHistoryScreenRoute = "chatHistory"


fun NavGraphBuilder.chatHistoryScreen(
    onBackClick: () -> Unit,
    onNavigateToChat: OnNavigateToChat
) {
    composable(route = chatHistoryScreenRoute) {
        val viewModel = hiltViewModel<ChatHistoryViewModel>()

        ChatHistoryScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNavigateToChat = onNavigateToChat
        )
    }
}

fun NavController.navigateToChatHistoryScreen(navOptions: NavOptions? = null) {
    navigate(route = chatHistoryScreenRoute, navOptions = navOptions)
}