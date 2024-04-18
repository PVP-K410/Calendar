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
import com.pvp.app.api.FriendService
import com.pvp.app.api.UserService
import com.pvp.app.di.AuthenticationModule
import com.pvp.app.model.AuthenticationResult
import com.pvp.app.model.FriendObject
import com.pvp.app.model.SignOutResult
import com.pvp.app.model.User
import com.pvp.app.model.UserProperties
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Named

class AuthenticationServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val client: SignInClient,
    private val friendService: FriendService,
    @Named(AuthenticationModule.INTENT_GOOGLE_SIGN_IN)
    private val intent: Intent,
    private val request: BeginSignInRequest,
    private val userService: UserService
) : AuthenticationService {

    override val user = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser)
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

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

    override suspend fun deleteAccount() {
        val user = auth.currentUser ?: return

        user.delete().await()
    }

    /**
     * If the [User] does not exist in the database, it is registered.
     */
    private suspend fun ifNotExistsRegister(
        user: FirebaseUser
    ) {
        val userApp = userService
            .get(user.email!!)
            .firstOrNull()

        if (userApp != null) {
            return
        }

        userService.merge(
            User(
                activities = emptyList(),
                email = user.email!!,
                height = 0,
                ingredients = emptyList(),
                mass = 0,
                points = 0,
                username = user.displayName ?: user.email!!.substringBefore("@")
            )
        )

        friendService.merge(
            FriendObject(
                friends = emptyList(),
                receivedRequests = emptyList(),
                sentRequests = emptyList()
            ),
            user.email!!
        )
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

    override suspend fun signIn(
        intent: Intent,
        isOneTap: Boolean
    ): AuthenticationResult {
        return try {
            val token = if (!isOneTap) {
                GoogleSignIn
                    .getSignedInAccountFromIntent(intent)
                    .await().idToken
            } else {
                client.getSignInCredentialFromIntent(intent).googleIdToken
            }

            val user = auth
                .signInWithCredential(
                    GoogleAuthProvider.getCredential(
                        token,
                        null
                    )
                )
                .await().user

            user.let {
                if (it != null) {
                    ifNotExistsRegister(it)
                }

                resolveAuthenticationResult(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed: ${e.message}")

            AuthenticationResult(messageError = e.message)
        }
    }

    override suspend fun signOut(
        onSignOut: suspend (SignOutResult) -> Unit
    ): SignOutResult {
        return try {
            client
                .signOut()
                .await()

            auth.signOut()

            SignOutResult(isSuccess = true)
                .also { onSignOut(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed: ${e.message}")

            if (e is CancellationException) {
                throw e
            }

            SignOutResult(messageError = e.message)
                .also { onSignOut(it) }
        }
    }
}