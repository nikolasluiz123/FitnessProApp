package br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator

import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState
import java.time.LocalDateTime

data class MessageDecorator(
    var id: String,
    var message: String,
    var date: LocalDateTime,
    var state: EnumMessageState,
    var userSenderId: String,
    var yourMessage: Boolean
)