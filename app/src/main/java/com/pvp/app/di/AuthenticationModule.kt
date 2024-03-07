package com.pvp.app.di

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pvp.app.api.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    const val INTENT_GOOGLE_SIGN_IN = "GoogleSignIn"

    @Named(INTENT_GOOGLE_SIGN_IN)
    @Provides
    @Singleton
    fun provideBeginSignInIntent(
        configuration: Configuration,
        @ApplicationContext context: Context
    ): Intent {
        val options = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(configuration.googleOAuthClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(
            context,
            options
        ).signInIntent
    }

    @Provides
    @Singleton
    fun provideBeginSignInRequest(
        configuration: Configuration
    ): BeginSignInRequest {
        return BeginSignInRequest
            .builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions
                    .builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(configuration.googleOAuthClientId)
                    .setSupported(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideSignInClient(
        @ApplicationContext context: Context
    ): SignInClient {
        return Identity.getSignInClient(context)
    }
}