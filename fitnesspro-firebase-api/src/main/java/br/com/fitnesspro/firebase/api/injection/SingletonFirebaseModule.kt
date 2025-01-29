package br.com.fitnesspro.firebase.api.injection

import android.content.Context
import br.com.fitnesspro.firebase.api.authentication.DefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.GoogleAuthenticationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonFirebaseModule {

    @Provides
    fun provideDefaultAuthenticationService(): DefaultAuthenticationService {
        return DefaultAuthenticationService()
    }

    @Provides
    fun provideGoogleAuthenticationService(
        @ApplicationContext context: Context
    ): GoogleAuthenticationService {
        return GoogleAuthenticationService(context)
    }

}