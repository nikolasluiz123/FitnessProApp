package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.repository.UserRepository
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
    ): UserRepository {
        return UserRepository(context = context)
    }

}