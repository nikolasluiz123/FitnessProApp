package br.com.fitnesspro.scheduler.ui.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.scheduler.ui.screen.chat.ChatScreen
import br.com.fitnesspro.scheduler.ui.viewmodel.ChatViewModel
import com.google.gson.GsonBuilder


internal const val chatScreenRoute = "chat"
internal const val chatArguments = "chatArguments"
private const val deepLinkChatRoute = "app://fitnesspro/chat?"

fun NavGraphBuilder.chatScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "$chatScreenRoute?$chatArguments={$chatArguments}",
        deepLinks = listOf(navDeepLink { uriPattern = "$deepLinkChatRoute$chatArguments={$chatArguments}" })
    ) {
        val viewModel = hiltViewModel<ChatViewModel>()

        ChatScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToChatScreen(args: ChatArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)

    navigate(route = "$chatScreenRoute?$chatArguments={$json}", navOptions = navOptions)
}

fun getChatScreenDeepLinkUri(args: ChatArgs): Uri {
    val json = GsonBuilder().defaultGSon().toJson(args)
    return "$deepLinkChatRoute$chatArguments={$json}".toUri()
}

data class ChatArgs(val chatId: String)