package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.mock.PersonMockHelper
import br.com.fitnesspro.usecase.person.SavePersonMockUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonMockModule {

    @Provides
    fun providePersonMockHelper(
        @ApplicationContext context: Context,
        savePersonMockUseCase: SavePersonMockUseCase
    ): PersonMockHelper {
        return PersonMockHelper(
            savePersonMockUseCase = savePersonMockUseCase
        )
    }
}