package br.com.fitnesspro.common.ui.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class DefaultGlobalEvents @Inject constructor() : GlobalEvents {
  private val _events = MutableSharedFlow<GlobalEvent>(extraBufferCapacity = 1)
  override val events: SharedFlow<GlobalEvent> = _events
  override suspend fun publish(event: GlobalEvent) {
    _events.emit(event)
  }
}