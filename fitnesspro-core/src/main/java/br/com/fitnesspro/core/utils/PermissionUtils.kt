package br.com.fitnesspro.core.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

/**
 * Object que contem funções utilitárias relacionadas a permissões
 * do android.
 *
 * @author Nikolas Luiz Schmitt
 */
object PermissionUtils {

    /**
     * Função que simplifica um pouco a solicitação de uma permissão,
     * tornando opcional a passagem de um callback executado no resultado.
     *
     * @see [rememberLauncherForActivityResult]
     *
     * @param onResult
     *
     * @author Nikolas Luiz Schmitt
     */
    @Composable
    fun requestPermissionLauncher(onResult: (Boolean) -> Unit = { }) = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission(), onResult)

    @Composable
    fun requestMultiplePermissionsLauncher(onResult: (Map<String, Boolean>) -> Unit = { }): ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> {
        return rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), onResult)
    }

    /**
     * Função responsável por retornar a permissão para leitura
     * de imagens de acordo com a versão do Android.
     *
     * @author Nikolas Luiz Schmitt
     */
    fun getMediaImagesPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

