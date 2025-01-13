package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.mock.PersonMockHelper
import br.com.fitnesspro.common.usecase.person.SavePersonMockUseCase
import com.github.javafaker.Faker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale

@Module
@InstallIn(SingletonComponent::class)
class SingletonCommonMockModule {

    @Provides
    fun providePersonMockHelper(
        savePersonMockUseCase: SavePersonMockUseCase,
        faker: Faker
    ): PersonMockHelper {
        return PersonMockHelper(
            savePersonMockUseCase = savePersonMockUseCase,
            faker = faker
        )
    }

    @Provides
    fun provideFaker(): Faker {
        return Faker(Locale.getDefault())
    }
}