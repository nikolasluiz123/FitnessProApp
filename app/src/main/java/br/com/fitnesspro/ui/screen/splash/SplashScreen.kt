package br.com.fitnesspro.ui.screen.splash

import android.graphics.drawable.AdaptiveIconDrawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import br.com.fitnesspro.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    SplashScreen(
        onNavigate = {
            viewModel.verifyNavigationDestination(
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToRoomList = onNavigateToHome
            )
        }
    )
}

@Composable
fun SplashScreen(
    onNavigate: () -> Unit = { }
) {

    LaunchedEffect(Unit) {
        delay(2000)
        onNavigate()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = adaptiveIconPainterResource(id = R.mipmap.ic_launcher),
            contentDescription = stringResource(R.string.splash_screen_ic_launcher_description),
            modifier = Modifier.size(160.dp)
        )
    }
}

@Composable
fun adaptiveIconPainterResource(@DrawableRes id: Int): Painter {
    val res = LocalContext.current.resources
    val theme = LocalContext.current.theme
    val adaptiveIcon = ResourcesCompat.getDrawable(res, id, theme) as AdaptiveIconDrawable

    return BitmapPainter(adaptiveIcon.toBitmap().asImageBitmap())
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SplashScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SplashScreen()
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SplashScreenPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            SplashScreen()
        }
    }
}