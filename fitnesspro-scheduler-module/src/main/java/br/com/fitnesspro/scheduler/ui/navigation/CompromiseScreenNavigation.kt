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
import br.com.fitnesspro.scheduler.ui.screen.chat.callbacks.OnNavigateToChat
import br.com.fitnesspro.scheduler.ui.screen.compromisse.CompromiseScreen
import br.com.fitnesspro.scheduler.ui.viewmodel.CompromiseViewModel
import com.google.gson.GsonBuilder
import java.time.LocalDate


internal const val compromiseScreenRoute = "compromise"
internal const val compromiseArguments = "compromiseArguments"
private const val deepLinkCompromiseRoute = "app://fitnesspro/compromise?"

fun NavGraphBuilder.compromiseScreen(
    onBackClick: () -> Unit,
    onNavigateToChat: OnNavigateToChat
) {
    composable(
        route = "$compromiseScreenRoute?$compromiseArguments={$compromiseArguments}",
        deepLinks = listOf(navDeepLink { uriPattern = "$deepLinkCompromiseRoute$compromiseArguments={$compromiseArguments}" })
    ) {
        val viewModel = hiltViewModel<CompromiseViewModel>()

        CompromiseScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNavigateToChat = onNavigateToChat
        )
    }
}

fun NavController.navigateToCompromiseScreen(
    args: CompromiseScreenArgs,
    navOptions: NavOptions? = null
) {
    val json = GsonBuilder().defaultGSon().toJson(args)

    navigate(route = "$compromiseScreenRoute?$compromiseArguments={$json}", navOptions = navOptions)
}

fun getCompromiseScreenDeepLinkUri(args: CompromiseScreenArgs): Uri {
    val json = GsonBuilder().defaultGSon().toJson(args)
    return "$deepLinkCompromiseRoute$compromiseArguments={$json}".toUri()
}

class CompromiseScreenArgs(
    val recurrent: Boolean,
    val date: LocalDate? = null,
    val schedulerId: String? = null
)