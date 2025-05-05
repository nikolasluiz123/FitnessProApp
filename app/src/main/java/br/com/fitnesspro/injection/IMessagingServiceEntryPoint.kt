package br.com.fitnesspro.injection

import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IMessagingServiceEntryPoint {

    fun getFirestoreChatRepository(): FirestoreChatRepository

    fun getPersonRepository(): PersonRepository

    fun getSchedulerRepository(): SchedulerRepository
}