package com.example.studyapp.auth

import android.app.Application
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.AndroidViewModel
import com.example.studyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.credentials.exceptions.NoCredentialException

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val context: Context = application.applicationContext

    private val _userDisplayName = MutableStateFlow<String?>(null)
    val userDisplayName: StateFlow<String?> = _userDisplayName

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl

    private val _userEmail = MutableStateFlow(auth.currentUser?.email)
    val userEmail: StateFlow<String?> = _userEmail

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    suspend fun signInWithGoogle(onSuccess: ()-> Unit = {}) {
        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = withContext(Dispatchers.IO) {
                credentialManager.getCredential(request = request, context = context)
            }

            val credential = result.credential
            if (credential is GoogleIdTokenCredential) {
                val idToken = credential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential).await()

                val user = auth.currentUser
                _isLoggedIn.value = user != null
                _userEmail.value = user?.email
                _userDisplayName.value = user?.displayName
                _userPhotoUrl.value = user?.photoUrl?.toString()

                _authError.value = "Login successful"
                onSuccess()
            }
        } catch (e: NoCredentialException) {
            _authError.value = "No credentials available. Make sure you’re signed into a Google account."
            _isLoggedIn.value = false
            _userEmail.value = null
        } catch (e: Exception) {
            _authError.value = "Login failed: ${e.localizedMessage}"
            _isLoggedIn.value = false
            _userEmail.value = null
        }
    }


    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
        _userEmail.value = null
    }
}
