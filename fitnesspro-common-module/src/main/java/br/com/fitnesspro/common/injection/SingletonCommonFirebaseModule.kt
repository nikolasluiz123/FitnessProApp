package br.com.fitnesspro.common.injection

import br.com.fitnesspro.firebase.api.authentication.DefaultAuthenticationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonCommonFirebaseModule {

    @Provides
    fun provideDefaultAuthenticationService(): DefaultAuthenticationService {
        return DefaultAuthenticationService()
    }
}