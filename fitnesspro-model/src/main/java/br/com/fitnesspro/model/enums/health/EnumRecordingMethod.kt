package br.com.fitnesspro.model.enums.health

import android.content.Context
import br.com.fitnesspro.model.R
import br.com.fitnesspro.model.enums.interfaces.IEnumDomain

enum class EnumRecordingMethod : IEnumDomain {
    RECORDING_METHOD_UNKNOWN {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_recording_method_unknown)
        }
    },
    RECORDING_METHOD_ACTIVELY_RECORDED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_recording_method_active)
        }
    },
    RECORDING_METHOD_AUTOMATICALLY_RECORDED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_recording_method_automatically)
        }
    },
    RECORDING_METHOD_MANUAL_ENTRY {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_recording_method_manual)
        }
    }
}