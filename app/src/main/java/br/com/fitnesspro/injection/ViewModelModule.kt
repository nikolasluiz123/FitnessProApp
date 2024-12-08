package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.helper.TransferObjectHelper
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.usecase.person.SavePersonUseCase
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
        personDAO: PersonDAO,
        userDAO: UserDAO
    ): UserRepository {
        return UserRepository(personDAO = personDAO, userDAO = userDAO)
    }

    @Provides
    fun provideTransferObjectHelper(
        @ApplicationContext context: Context,
    ): TransferObjectHelper {
        return TransferObjectHelper(context)
    }

    @Provides
    fun provideSavePersonUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        toHelper: TransferObjectHelper
    ): SavePersonUseCase {
        return SavePersonUseCase(
            context = context,
            userRepository = userRepository,
            toHelper = toHelper
        )
    }

}