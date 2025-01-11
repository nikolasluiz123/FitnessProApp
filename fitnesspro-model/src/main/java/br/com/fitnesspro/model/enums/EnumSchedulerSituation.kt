package br.com.fitnesspro.model.enums

import android.content.Context
import br.com.fitnesspro.model.R
import br.com.fitnesspro.model.enums.interfaces.IEnumDomain

enum class EnumSchedulerSituation : IEnumDomain {
    SCHEDULED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_scheduler_situation_scheduled)
        }
    },
    CANCELLED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_scheduler_situation_cancelled)
        }
    },
    CONFIRMED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_scheduler_situation_confirmed)
        }
    },
    COMPLETED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_scheduler_situation_completed)
        }
    }
}