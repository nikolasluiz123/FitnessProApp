package br.com.fitnesspro.common.injection

import android.content.Context
import br.com.fitnesspro.common.mock.PersonMockHelper
import br.com.fitnesspro.common.usecase.person.SavePersonMockUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonCommonMockModule {

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