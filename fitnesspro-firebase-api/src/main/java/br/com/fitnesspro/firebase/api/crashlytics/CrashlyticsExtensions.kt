package br.com.fitnesspro.firebase.api.crashlytics

import br.com.fitnesspro.core.exceptions.NoLoggingException
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

fun Throwable.sendToFirebaseCrashlytics() {
    if (this !is NoLoggingException) {
        Firebase.crashlytics.recordException(this)
    }
}