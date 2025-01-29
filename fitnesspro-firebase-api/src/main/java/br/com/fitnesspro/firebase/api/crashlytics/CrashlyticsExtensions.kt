package br.com.fitnesspro.firebase.api.crashlytics

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun Throwable.sendToFirebaseCrashlytics() {
    Firebase.crashlytics.recordException(this)
}