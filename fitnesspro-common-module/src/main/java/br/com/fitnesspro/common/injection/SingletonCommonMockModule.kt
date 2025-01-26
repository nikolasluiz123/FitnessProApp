package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.mock.PersonMockHelper
import br.com.fitnesspro.common.usecase.person.SavePersonBatchUseCase
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
        savePersonBatchUseCase: SavePersonBatchUseCase,
        faker: Faker
    ): PersonMockHelper {
        return PersonMockHelper(
            savePersonBatchUseCase = savePersonBatchUseCase,
            faker = faker
        )
    }

    @Provides
    fun provideFaker(): Faker {
        return Faker(Locale.getDefault())
    }
}