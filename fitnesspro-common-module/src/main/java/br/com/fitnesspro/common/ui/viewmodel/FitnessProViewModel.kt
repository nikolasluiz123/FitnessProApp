package br.com.fitnesspro.common.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.ui.event.GlobalEvent
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.shared.communication.exception.ExpiredTokenException
import br.com.fitnesspro.shared.communication.exception.NotFoundTokenException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class FitnessProViewModel : ViewModel() {

    abstract fun onShowError(throwable: Throwable)

    abstract fun getGlobalEventsBus(): GlobalEvents

    private fun onShowCommonError(throwable: Throwable) {
        when(throwable) {
            is ExpiredTokenException,
            is NotFoundTokenException -> { }
            else -> onShowError(throwable)
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onShowCommonError(throwable)
        onError(throwable)
    }

    protected fun onError(throwable: Throwable) {
        when (throwable) {
            is ExpiredTokenException,
            is NotFoundTokenException -> {
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
            getGlobalEventsBus().publish(GlobalEvent.TokenExpired)
        }
    }

    companion object {
        private const val TAG = "FitnessProViewModel"
    }

}