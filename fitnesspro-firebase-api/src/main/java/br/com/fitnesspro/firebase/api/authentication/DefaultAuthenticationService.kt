package br.com.fitnesspro.firebase.api.authentication

import br.com.fitnesspro.model.general.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DefaultAuthenticationService {

    fun authenticate(
        email: String,
        password: String,
        onSuccess: (AuthResult) -> Unit = { },
        onFailure: (Exception) -> Unit = { }
    ) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun register(
        email: String,
        password: String,
        onSuccess: (AuthResult) -> Unit = { },
        onFailure: (Exception) -> Unit = { }
    ) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun updateUserInfos(user: User) {
        Firebase.auth.currentUser?.let { firebaseUser ->
            firebaseUser.verifyBeforeUpdateEmail(user.email!!)
            firebaseUser.updatePassword(user.password!!)
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}