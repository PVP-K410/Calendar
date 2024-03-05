package com.pvp.app.api

import android.content.Intent
import android.content.IntentSender
import com.google.firebase.auth.FirebaseUser
import com.pvp.app.model.AuthenticationResult
import com.pvp.app.model.SignOutResult
import kotlinx.coroutines.flow.Flow

interface AuthenticationService {

    /**
     * @return the currently signed-in user if the user is authenticated, otherwise returns null.
     */
    val user: Flow<FirebaseUser?>

    /**
     *  Begins the authentication process. If the authentication process is started successfully,
     *  the returned [IntentSender] can be used to start the sign-up or sign-in flow.
     *
     *  @return an [IntentSender] if the authentication process is started successfully, otherwise
     *  returns null.
     */
    suspend fun beginSignIn(): IntentSender?

    /**
     * Completes the sign-in process.
     *
     * @param intent The [Intent] that contains the reference to the user's data.
     * @param onSignIn A callback that is called when the sign-in process is completed, but before
     * the user flow is triggered.
     */
    suspend fun signIn(
        intent: Intent,
        onSignIn: suspend (AuthenticationResult) -> Unit = {}
    ): AuthenticationResult

    /**
     * Signs out the currently signed-in user.
     *
     * @param onSignOut A callback that is called when the sign-out process is completed, but
     * before the user flow is triggered.
     */
    suspend fun signOut(onSignOut: suspend (SignOutResult) -> Unit = {}): SignOutResult
}