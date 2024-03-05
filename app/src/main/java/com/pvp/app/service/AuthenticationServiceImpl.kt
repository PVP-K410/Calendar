package com.pvp.app.service

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pvp.app.api.AuthenticationService
import com.pvp.app.model.AuthenticationResult
import com.pvp.app.model.SignOutResult
import com.pvp.app.model.UserProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val client: SignInClient,
    private val request: BeginSignInRequest
) : AuthenticationService {

    private val _user = MutableStateFlow(auth.currentUser)
    override val user = _user.asStateFlow()

    override suspend fun beginSignIn(): IntentSender? {
        val result = try {
            client
                .beginSignIn(request)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()

            if (e is CancellationException) {
                throw e
            }

            null
        }

        return result?.pendingIntent?.intentSender
    }

    override suspend fun signIn(
        intent: Intent,
        onSignIn: suspend (AuthenticationResult) -> Unit
    ): AuthenticationResult {
        return try {
            val credentials = client.getSignInCredentialFromIntent(intent)
            val googleId = credentials.googleIdToken
            val googleCredentials = GoogleAuthProvider.getCredential(googleId, null)

            val user = auth
                .signInWithCredential(googleCredentials)
                .await().user

            val result = AuthenticationResult(
                data = user?.run {
                    UserProperties(
                        email = email!!,
                        id = uid,
                        username = displayName ?: email!!.substringBefore("@")
                    )
                },
                isSuccess = true
            )

            onSignIn(result)

            _user.value = user

            result
        } catch (e: Exception) {
            e.printStackTrace()

            if (e is CancellationException) {
                throw e
            }

            val result = AuthenticationResult(messageError = e.message)

            onSignIn(result)

            result
        }
    }

    override suspend fun signOut(onSignOut: suspend (SignOutResult) -> Unit): SignOutResult {
        return try {
            client
                .signOut()
                .await()

            auth.signOut()

            val result = SignOutResult(isSuccess = true)

            onSignOut(result)

            _user.value = null

            result
        } catch (e: Exception) {
            e.printStackTrace()

            if (e is CancellationException) {
                throw e
            }

            val result = SignOutResult(messageError = e.message)

            onSignOut(result)

            result
        }
    }
}