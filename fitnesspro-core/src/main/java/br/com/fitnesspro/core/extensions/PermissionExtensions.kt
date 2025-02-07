package br.com.fitnesspro.core.extensions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import br.com.fitnesspro.core.utils.PermissionUtils

/**
 * Função que retorna se a permissão foi consedida ou não.
 *
 * @param permission Permissão que será verificada. [Manifest.permission]
 *
 * @author Nikolas Luiz Schmitt
 */
fun Context.verifyPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * Função que verifica se a permissão da câmera foi concedida
 *
 * @author Nikolas Luiz Schmitt
 */
fun Context.verifyCameraPermissionGranted(): Boolean {
    return verifyPermissionGranted(Manifest.permission.CAMERA)
}

/**
 * Função que verifica se a permissão de leitura de imagens
 * foi concedida, de acordo com a versão do android.
 *
 * @author Nikolas Luiz Schmitt
 */
fun Context.verifyGalleryPermissionGranted(): Boolean {
    return verifyPermissionGranted(PermissionUtils.getMediaImagesPermission())
}

fun Context.verifyWriteExternalStoragePermissionGranted(): Boolean {
    return verifyPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

/**
 * Função que executa um launcher da galeria do android para exibir apenas
 * imagens.
 *
 * @author Nikolas Luiz Schmitt
 */
fun ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>.launchImageOnly() {
    this.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

/**
 * Função que executa um launcher para solicitar a permissão da cãmera.
 *
 * @author Nikolas Luiz Schmitt
 */
fun ManagedActivityResultLauncher<String, Boolean>.requestCameraPermission() {
    this.launch(Manifest.permission.CAMERA)
}

/**
 * Função que executa um launcher para solicitar a permissão da galeria
 *
 * @author Nikolas Luiz Schmitt
 */
fun ManagedActivityResultLauncher<String, Boolean>.requestGalleryPermission() {
    this.launch(PermissionUtils.getMediaImagesPermission())
}

fun ManagedActivityResultLauncher<String, Boolean>.requestWriteExternalStoragePermission() {
    this.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}