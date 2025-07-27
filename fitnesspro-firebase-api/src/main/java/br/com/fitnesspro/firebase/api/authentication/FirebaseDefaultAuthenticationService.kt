package br.com.fitnesspro.firebase.api.authentication

import android.content.Context
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.firebase.api.R
import br.com.fitnesspro.model.general.User
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseDefaultAuthenticationService {

    suspend fun authenticate(email: String, password: String): AuthResult? = withContext(IO) {
        try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
        } catch (ex: FirebaseNetworkException) {
            null
        }
    }

    suspend fun register(email: String, password: String): AuthResult? = withContext(IO) {
        try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        } catch (ex: FirebaseNetworkException) {
            null
        }
    }

    suspend fun updateUserInfos(context: Context,user: User): Unit = withContext(IO) {
        Firebase.auth.currentUser?.let { firebaseUser ->
            try {
                firebaseUser.verifyBeforeUpdateEmail(user.email!!).await()
                firebaseUser.updatePassword(user.password!!).await()
            } catch (_: FirebaseAuthInvalidUserException) {
                if (!context.isNetworkAvailable()) {
                    throw FirebaseNetworkException(context.getString(R.string.validation_msg_require_reauthentication))
                }

                logout()
                authenticate(user.email!!, user.password!!)
            }
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}