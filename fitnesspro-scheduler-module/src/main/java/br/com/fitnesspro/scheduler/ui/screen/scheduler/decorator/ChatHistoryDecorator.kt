package br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator

import java.time.LocalDateTime

data class ChatHistoryDecorator(
    var id: String,
    var userName: String,
    var notReadMessagesCount: Int,
    var lastMessage: String,
    var lastMessageDate: LocalDateTime
)