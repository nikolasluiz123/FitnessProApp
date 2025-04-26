package br.com.fitnesspro.common.ui.event

import kotlinx.coroutines.flow.SharedFlow

interface GlobalEvents {
  val events: SharedFlow<GlobalEvent>
  suspend fun publish(event: GlobalEvent)
}