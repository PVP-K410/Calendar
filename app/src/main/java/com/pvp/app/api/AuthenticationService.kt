package com.pvp.app.api

import android.content.Intent
import android.content.IntentSender
import com.google.firebase.auth.FirebaseUser
import com.pvp.app.model.AuthenticationResult

interface AuthenticationService {

    /**
     * @return the currently signed-in user if the user is authenticated, otherwise returns null.
     */
    val user: FirebaseUser?

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
     *
     * @return an [AuthenticationResult] with the user's data if the sign-in process is completed
     * successfully, otherwise returns an [AuthenticationResult] with an error message.
     */
    suspend fun signIn(intent: Intent): AuthenticationResult

    /**
     * Signs out the currently signed-in user.
     */
    suspend fun signOut()
}