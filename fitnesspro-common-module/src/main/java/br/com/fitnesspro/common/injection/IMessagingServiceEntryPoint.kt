package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IMessagingServiceEntryPoint {

    fun getFirestoreChatRepository(): FirestoreChatRepository

    fun getPersonRepository(): PersonRepository
}