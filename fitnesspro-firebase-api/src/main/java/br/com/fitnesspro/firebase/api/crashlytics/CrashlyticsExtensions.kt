package br.com.fitnesspro.firebase.api.crashlytics

import br.com.fitnesspro.core.exceptions.NoLoggingException
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun Throwable.sendToFirebaseCrashlytics() {
    if (this !is NoLoggingException) {
        Firebase.crashlytics.recordException(this)
    }
}