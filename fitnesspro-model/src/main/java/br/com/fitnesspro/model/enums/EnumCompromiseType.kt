package br.com.fitnesspro.model.enums

import android.content.Context
import br.com.android.room.toolkit.model.interfaces.IEnumDomain
import br.com.fitnesspro.model.R

enum class EnumCompromiseType : IEnumDomain {
    FIRST {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_compromise_type_first)
        }
    },
    RECURRENT {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_compromise_type_recurrent)
        }
    }
}