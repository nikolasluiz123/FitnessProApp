package br.com.fitnesspro.common.injection

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
    fun provideFaker(): Faker {
        return Faker(Locale.getDefault())
    }
}