package br.com.fitnesspro.common.ui.viewmodel.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.ui.event.GlobalEvent
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.shared.communication.exception.ExpiredTokenException
import br.com.fitnesspro.shared.communication.exception.NotFoundTokenException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class FitnessProViewModel : ViewModel() {

    abstract fun getErrorMessageFrom(throwable: Throwable): String

    abstract fun onShowErrorDialog(message: String)

    abstract fun getGlobalEventsBus(): GlobalEvents

    private fun onShowCommonError(throwable: Throwable) {
        val message = when(throwable) {
            is ExpiredTokenException,
            is NotFoundTokenException -> null
            is ServiceException -> throwable.message!!
            else -> getErrorMessageFrom(throwable)
        }

        message?.let(::onShowErrorDialog)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onShowCommonError(throwable)
        onError(throwable)
    }

    protected open fun onError(throwable: Throwable) {
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