package br.com.fitnesspro.common.ui.screen.login

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import br.com.core.android.compose.utils.extensions.verifyPermissionGranted
import br.com.core.android.compose.utils.permission.PermissionUtils.requestMultiplePermissionsLauncher

@Composable
fun RequestAllPermissions(context: Context) {
    val standardPermissionLauncher = requestMultiplePermissionsLauncher()

    LaunchedEffect(Unit) {
        val standardPermissions = mutableListOf<String>()
        addNotificationsPermission(context, standardPermissions)
        addCameraPermission(context, standardPermissions)
        addMediaPermissions(context, standardPermissions)
        addRecordAudioPermission(context, standardPermissions)

        if (standardPermissions.isNotEmpty()) {
            standardPermissionLauncher.launch(standardPermissions.toTypedArray())
        }
    }
}

private fun addRecordAudioPermission(
    context: Context,
    permissions: MutableList<String>
) {
    if (!context.verifyPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
        permissions.add(Manifest.permission.RECORD_AUDIO)
    }
}

private fun addMediaPermissions(
    context: Context,
    permissions: MutableList<String>
) {
    val requiredPermissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
            READ_MEDIA_IMAGES,
            READ_MEDIA_VIDEO,
            READ_MEDIA_VISUAL_USER_SELECTED
        )

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
            READ_MEDIA_IMAGES,
            READ_MEDIA_VIDEO
        )

        else -> listOf(READ_EXTERNAL_STORAGE)
    }

    requiredPermissions
        .filterNot { context.verifyPermissionGranted(it) }
        .forEach { permissions.add(it) }
}

private fun addCameraPermission(
    context: Context,
    permissions: MutableList<String>
) {
    if (!context.verifyPermissionGranted(Manifest.permission.CAMERA)) {
        permissions.add(Manifest.permission.CAMERA)
    }
}

private fun addNotificationsPermission(
    context: Context,
    permissions: MutableList<String>
) {
    if (!context.verifyPermissionGranted(Manifest.permission.POST_NOTIFICATIONS) &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    ) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
fun rememberInstallHealthConnectLauncher(): () -> Unit {
    val context = LocalContext.current
    val providerPackageName = "com.google.android.apps.healthdata"

    return {
        val marketIntent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$providerPackageName".toUri()
        )

        val webIntent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$providerPackageName".toUri()
        )

        val finalIntent = if (marketIntent.resolveActivity(context.packageManager) != null) {
            marketIntent
        } else {
            webIntent
        }

        context.startActivity(finalIntent)
    }
}