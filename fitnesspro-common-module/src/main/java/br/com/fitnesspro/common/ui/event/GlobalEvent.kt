package br.com.fitnesspro.common.ui.event

sealed class GlobalEvent {
    object TokenExpired : GlobalEvent()
}
