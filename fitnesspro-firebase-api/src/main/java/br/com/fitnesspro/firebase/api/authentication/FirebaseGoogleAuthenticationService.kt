package br.com.fitnesspro.firebase.api.authentication

import android.content.Context
import br.com.android.firebase.toolkit.authentication.AbstractFirebaseGoogleAuthenticationService
import br.com.fitnesspro.firebase.api.BuildConfig

class FirebaseGoogleAuthenticationService(context: Context): AbstractFirebaseGoogleAuthenticationService(context) {

    override fun getServerClientId(): String {
        return BuildConfig.WEB_CLIENT_ID
    }
}