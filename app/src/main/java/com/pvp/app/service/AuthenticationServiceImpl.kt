package com.pvp.app.service

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pvp.app.api.AuthenticationService
import com.pvp.app.model.AuthenticationResult
import com.pvp.app.model.UserProperties
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val client: SignInClient,
    private val request: BeginSignInRequest
) : AuthenticationService {

    override val user = auth.currentUser

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

    override suspend fun signIn(intent: Intent): AuthenticationResult {
        return try {
            val credentials = client.getSignInCredentialFromIntent(intent)
            val googleId = credentials.googleIdToken
            val googleCredentials = GoogleAuthProvider.getCredential(googleId, null)

            val user = auth
                .signInWithCredential(googleCredentials)
                .await().user

            AuthenticationResult(
                data = user?.run {
                    UserProperties(
                        email = email!!,
                        id = uid,
                        username = displayName ?: email!!.substringBefore("@")
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()

            if (e is CancellationException) {
                throw e
            }

            AuthenticationResult(messageError = e.message)
        }
    }

    override suspend fun signOut() {
        try {
            client
                .signOut()
                .await()

            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()

            if (e is CancellationException) {
                throw e
            }
        }
    }
}