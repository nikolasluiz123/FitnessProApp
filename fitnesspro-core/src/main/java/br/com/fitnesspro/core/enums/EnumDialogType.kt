package br.com.fitnesspro.core.enums

import br.com.fitnesspro.core.R


enum class EnumDialogType(val titleResId: Int) {
    ERROR(R.string.error_dialog_title), CONFIRMATION(R.string.warning_dialog_title)
}