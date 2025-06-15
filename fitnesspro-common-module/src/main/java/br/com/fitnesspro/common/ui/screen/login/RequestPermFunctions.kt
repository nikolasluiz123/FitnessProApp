package br.com.fitnesspro.common.ui.screen.login

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.com.fitnesspro.core.extensions.verifyPermissionGranted
import br.com.fitnesspro.core.utils.PermissionUtils.requestMultiplePermissionsLauncher

@Composable
fun RequestAllPermissions(context: Context) {
    val requestPermissionLauncher = requestMultiplePermissionsLauncher()

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()

        addNotificationsPermission(context, permissions)
        addCameraPermission(context, permissions)
        addMediaPermissions(context, permissions)
        addRecordAudioPermission(context, permissions)

        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
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