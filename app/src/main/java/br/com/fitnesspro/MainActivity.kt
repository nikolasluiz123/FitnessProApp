package br.com.fitnesspro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import br.com.fitnesspro.common.ui.screen.login.AuthenticationBottomSheet
import br.com.fitnesspro.common.ui.screen.login.LoginScreen
import br.com.fitnesspro.common.ui.viewmodel.BottomSheetAuthenticationViewModel
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.ui.navigation.FitnessProNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessProTheme {
                App {
                    FitnessProNavHost(navController = rememberNavController())
                }
            }
        }
    }
}

@Composable
fun App(content: @Composable () -> Unit = { LoginScreen() }) {
    val viewModel: BottomSheetAuthenticationViewModel = hiltViewModel()

    AuthenticationBottomSheet(viewModel = viewModel)

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            content()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun AppPreview() {
    FitnessProTheme {
        Surface {
            App()
        }
    }
}