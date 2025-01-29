package br.com.fitnesspro.common.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class FitnessProViewModel : ViewModel() {

    abstract fun onShowError(throwable: Throwable)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onShowError(throwable)
        throwable.sendToFirebaseCrashlytics()
        Log.e(TAG, throwable.stackTraceToString())
    }

    fun launch(block: suspend () -> Unit) = viewModelScope.launch(exceptionHandler) {
        block()
    }

    companion object {
        private const val TAG = "FitnessProViewModel"
    }

}