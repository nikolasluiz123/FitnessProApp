package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.service.data.access.webclients.UserWebClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideUserRepository(
        @ApplicationContext context: Context,
        webClient: UserWebClient
    ): UserRepository {
        return UserRepository(context = context, webClient = webClient)
    }

}