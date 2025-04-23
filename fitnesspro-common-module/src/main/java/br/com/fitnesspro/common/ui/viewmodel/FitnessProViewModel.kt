package br.com.fitnesspro.common.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.ui.event.GlobalEvent
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.shared.communication.exception.ExpiredTokenException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class FitnessProViewModel : ViewModel() {

    private val _globalEvents = MutableSharedFlow<GlobalEvent>()
    val globalEvents: SharedFlow<GlobalEvent> = _globalEvents.asSharedFlow()

    abstract fun onShowError(throwable: Throwable)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onShowError(throwable)
        onError(throwable)
    }

    protected fun onError(throwable: Throwable) {
        when (throwable) {
            is ExpiredTokenException -> {
                notifyTokenExpired()
            }

            else -> {
                throwable.sendToFirebaseCrashlytics()
            }
        }

        Log.e(TAG, throwable.stackTraceToString())
    }

    fun launch(block: suspend () -> Unit) = viewModelScope.launch(exceptionHandler) {
        block()
    }

    protected fun notifyTokenExpired() {
        viewModelScope.launch {
            _globalEvents.emit(GlobalEvent.TokenExpired)
        }
    }

    companion object {
        private const val TAG = "FitnessProViewModel"
    }

}