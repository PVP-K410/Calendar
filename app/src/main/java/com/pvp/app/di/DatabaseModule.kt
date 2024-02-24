package com.pvp.app.di

import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.app
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
            .apply {
                firestoreSettings = firestoreSettings {
                    setLocalCacheSettings(
                        PersistentCacheSettings.newBuilder()
                            .build()
                    )
                }
            }
    }

    @Provides
    @Singleton
    fun provideFirebaseApp(): FirebaseApp {
        return Firebase.app
    }
}