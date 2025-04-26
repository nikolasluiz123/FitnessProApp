package br.com.fitnesspro.ui.screen.mock

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.buttons.FitnessProButton
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.ui.viewmodel.MockViewModel

@Composable
fun MockScreen(viewModel: MockViewModel) {
    MockScreen(
        onMockPersonClick = viewModel::executePersonMock
    )
}

@Composable
fun MockScreen(
    onMockPersonClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FitnessProButton(
            label = stringResource(id = R.string.mock_person),
            onClickListener = onMockPersonClick,
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MockScreenPreview() {
    FitnessProTheme {
        Surface {
            MockScreen()
        }
    }
}