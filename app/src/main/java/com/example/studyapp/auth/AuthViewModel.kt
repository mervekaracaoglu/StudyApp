package com.example.studyapp.auth

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * ViewModel for handling authentication logic with Firebase.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val context = application.applicationContext
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    private val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("192093597100-9flugc9jjhn90e2h6trpv6ofcisnjhip.apps.googleusercontent.com") // Replace with your Web Client ID from Firebase Console
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    /**
     * Launches Google One Tap Sign-In.
     */
    fun beginGoogleSignIn(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onError: (String) -> Unit
    ) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                launcher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Failed to launch Google sign-in.")
            }
    }

    /**
     * Handles the result from One Tap sign-in and signs in with Firebase.
     */
    fun handleGoogleSignInResult(
        data: Intent?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Firebase sign-in failed") }
            } else {
                onError("No Google ID token found.")
            }
        } catch (e: Exception) {
            onError(e.message ?: "Sign-in failed")
        }
    }

    /**
     * Logs in using email and password.
     */
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Login failed") }
    }

    /**
     * Registers a new user with email and password.
     */
    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Sign-up failed") }
    }

    /**
     * Signs out from Firebase.
     */
    fun signOut() {
        auth.signOut()
        oneTapClient.signOut()
    }
}
