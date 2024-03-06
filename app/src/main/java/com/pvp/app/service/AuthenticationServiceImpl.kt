package com.pvp.app.service

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.di.AuthenticationModule
import com.pvp.app.model.AuthenticationResult
import com.pvp.app.model.SignOutResult
import com.pvp.app.model.UserProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Named

class AuthenticationServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val client: SignInClient,
    @Named(AuthenticationModule.INTENT_GOOGLE_SIGN_IN)
    private val intent: Intent,
    private val request: BeginSignInRequest,
    private val userService: UserService
) : AuthenticationService {

    private val _user = MutableStateFlow(auth.currentUser)
    override val user = _user.asStateFlow()

    companion object {

        private const val TAG = "AuthenticationService"
    }

    override suspend fun beginSignIn(): Intent {
        return intent
    }

    override suspend fun beginSignInOneTap(): IntentSender? {
        val result = try {
            client
                .beginSignIn(request)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Begin sign in failed: ${e.message}")

            if (e is CancellationException) {
                throw e
            }

            null
        }

        return result?.pendingIntent?.intentSender
    }

    private fun resolveAuthenticationResult(user: FirebaseUser?): AuthenticationResult {
        return AuthenticationResult(
            isSuccess = user != null,
            properties = user?.run {
                UserProperties(
                    email = email!!,
                    id = uid,
                    username = displayName ?: email!!.substringBefore("@")
                )
            }
        )
    }

    private suspend fun resolveAndValidateUser(
        intent: Intent,
        isOneTap: Boolean,
        onValidate: suspend (String) -> Unit
    ): FirebaseUser? {
        val token = if (!isOneTap) {
            val client = GoogleSignIn
                .getSignedInAccountFromIntent(intent)
                .await()

            onValidate(client.email!!)

            client.idToken
        } else {
            val client = client.getSignInCredentialFromIntent(intent)

            onValidate(client.id)

            client.googleIdToken
        }

        return auth
            .signInWithCredential(GoogleAuthProvider.getCredential(token, null))
            .await().user
    }

    override suspend fun signIn(
        intent: Intent,
        isOneTap: Boolean,
        onSignIn: suspend (AuthenticationResult) -> Unit,
        onValidate: suspend (String) -> Unit
    ): AuthenticationResult {
        return try {
            resolveAndValidateUser(
                intent,
                isOneTap,
                onValidate
            ).run {
                resolveAuthenticationResult(this)
                    .also {
                        onSignIn(it)

                        _user.value = this
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed: ${e.message}")

            AuthenticationResult(messageError = e.message)
                .also { onSignIn(it) }
        }
    }

    override suspend fun signOut(onSignOut: suspend (SignOutResult) -> Unit): SignOutResult {
        return try {
            client
                .signOut()
                .await()

            auth.signOut()

            SignOutResult(isSuccess = true)
                .also {
                    onSignOut(it)

                    _user.value = null
                }
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed: ${e.message}")

            if (e is CancellationException) {
                throw e
            }

            SignOutResult(messageError = e.message)
                .also { onSignOut(it) }
        }
    }

    override suspend fun validateSignIn(email: String) {
        userService
            .get(email)
            .firstOrNull()
            ?: throw IllegalStateException("Registered user not found. Please sign up first")
    }

    override suspend fun validateSignUp(email: String) {
        userService
            .get(email)
            .firstOrNull()
            ?.run {
                throw IllegalStateException("User already exists. Please sign in instead")
            }
    }
}