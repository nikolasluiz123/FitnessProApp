package br.com.fitnesspro.firebase.api.authentication

import br.com.fitnesspro.model.general.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthenticationService {

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

    suspend fun updateUserInfos(user: User): Unit = withContext(IO) {
        Firebase.auth.currentUser?.let { firebaseUser ->
            firebaseUser.verifyBeforeUpdateEmail(user.email!!).await()
            firebaseUser.updatePassword(user.password!!).await()
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}