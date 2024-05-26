package br.com.fitnesspro.injection

import br.com.fitnesspro.local.access.UserDao
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.usecase.RegisterUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(
            userDao = userDao
        )
    }

    @Provides
    fun provideRegisterUserUseCase(userRepository: UserRepository): RegisterUserUseCase {
        return RegisterUserUseCase(
            userRepository = userRepository
        )
    }
}