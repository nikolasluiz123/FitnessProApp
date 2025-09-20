package br.com.fitnesspro.model.enums.health

import android.content.Context
import br.com.fitnesspro.model.R
import br.com.fitnesspro.model.enums.interfaces.IEnumDomain

enum class EnumSleepStage : IEnumDomain {
    UNKNOWN {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_unknown)
        }
    },
    SLEEPING {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_sleeping)
        }
    },
    OUT_OF_BED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_out_of_bed)
        }
    },
    AWAKE {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_awake)
        }
    },
    LIGHT {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_light)
        }
    },
    DEEP {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_deep)
        }
    },
    REM {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_rem)
        }
    },
    AWAKE_IN_BED {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_sleep_stage_awake_in_bed)
        }

    }
}