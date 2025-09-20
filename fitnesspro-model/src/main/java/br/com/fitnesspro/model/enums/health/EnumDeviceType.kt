package br.com.fitnesspro.model.enums.health

import android.content.Context
import br.com.fitnesspro.model.R
import br.com.fitnesspro.model.enums.interfaces.IEnumDomain

enum class EnumDeviceType: IEnumDomain {
    UNKNOWN {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_unknown)
        }
    },
    WATCH {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_watch)
        }
    },
    PHONE {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_phone)
        }
    },
    SCALE {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_scale)
        }
    },
    RING {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_ring)
        }
    },
    HEAD_MOUNTED {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_head_mounted)
        }
    },
    FITNESS_BAND {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_fitness_band)
        }
    },
    CHEST_STRAP {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_chest_strap)
        }
    },
    SMART_DISPLAY {
        override fun getLabel(context: Context): String? {
            return context.getString(R.string.enum_device_type_smart_display)
        }
    },
}