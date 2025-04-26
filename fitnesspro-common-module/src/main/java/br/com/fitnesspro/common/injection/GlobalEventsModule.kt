package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.ui.event.DefaultGlobalEvents
import br.com.fitnesspro.common.ui.event.GlobalEvents
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GlobalEventsModule {

  @Provides
  @Singleton
  fun provideGlobalEvents(): GlobalEvents = DefaultGlobalEvents()
}