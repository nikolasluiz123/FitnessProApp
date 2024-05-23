package br.com.fitnesspro.core.state

interface ILoadingUIState {
    val showLoading: Boolean
    val onToggleLoading: () -> Unit
}