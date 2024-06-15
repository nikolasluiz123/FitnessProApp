package br.com.fitnesspro.injection

import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.service.data.access.webclients.UserWebClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideUserRepository(webClient: UserWebClient): UserRepository {
        return UserRepository(webClient)
    }

}