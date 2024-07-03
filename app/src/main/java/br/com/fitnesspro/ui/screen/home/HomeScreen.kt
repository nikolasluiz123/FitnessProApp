package br.com.fitnesspro.ui.screen.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.ui.screen.home.callback.OnMyInformationsClick
import br.com.fitnesspro.ui.state.HomeUIState
import br.com.fitnesspro.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMyInformationsClick: OnMyInformationsClick
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreen(
        state = state,
        onMyInformationsClick = onMyInformationsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUIState = HomeUIState(),
    onMyInformationsClick: OnMyInformationsClick? = null
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = "",
                showNavigationIcon = false,
                showMenuWithLogout = false,
                showMenu = true,
                menuItems = {
                    DropdownMenuItem(
                        text = { Text(text = "Minhas Informações") },
                        onClick = {
                            onMyInformationsClick?.onExecute(
                                args = RegisterUserScreenArgs()
                            )
                        }
                    )
                }
            )
        }
    ) { padding ->
        ConstraintLayout(modifier = Modifier.padding(padding)) {

        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    FitnessProTheme {
        Surface {
            HomeScreen()
        }
    }
}