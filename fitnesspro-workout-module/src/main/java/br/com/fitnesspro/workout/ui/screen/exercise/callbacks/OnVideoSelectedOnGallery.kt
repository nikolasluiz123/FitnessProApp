package br.com.fitnesspro.workout.ui.screen.exercise.callbacks

import android.net.Uri

fun interface OnVideoSelectedOnGallery {
    fun onExecute(uri: Uri, onSuccess: () -> Unit)
}