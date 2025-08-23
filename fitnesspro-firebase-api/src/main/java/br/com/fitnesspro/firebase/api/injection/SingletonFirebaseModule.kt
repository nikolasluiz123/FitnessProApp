package br.com.fitnesspro.firebase.api.injection

import android.content.Context
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.FirebaseGoogleAuthenticationService
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import br.com.fitnesspro.firebase.api.firestore.service.FirestoreChatService
import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonFirebaseModule {

    @Provides
    fun provideDefaultAuthenticationService(): FirebaseDefaultAuthenticationService {
        return FirebaseDefaultAuthenticationService()
    }

    @Provides
    fun provideGoogleAuthenticationService(
        @ApplicationContext context: Context
    ): FirebaseGoogleAuthenticationService {
        return FirebaseGoogleAuthenticationService(context)
    }

    @Provides
    fun provideFirestoreChatService(): FirestoreChatService {
        return FirestoreChatService()
    }

    @Provides
    fun provideFirestoreChatRepository(
        firestoreChatService: FirestoreChatService
    ): FirestoreChatRepository {
        return FirestoreChatRepository(firestoreChatService)
    }

    @Provides
    fun provideStorageBucketService(
        @ApplicationContext context: Context
    ): StorageBucketService {
        return StorageBucketService(context)
    }
}