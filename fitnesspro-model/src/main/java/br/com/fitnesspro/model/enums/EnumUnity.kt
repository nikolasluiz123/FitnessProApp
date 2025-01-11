package br.com.fitnesspro.model.enums

import android.content.Context
import br.com.fitnesspro.model.R
import br.com.fitnesspro.model.enums.interfaces.IEnumDomain

enum class EnumUnity: IEnumDomain {
    GRAMS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_grams, quantity)
        }
    },
    KILOGRAMS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_kilograms, quantity)
        }
    },
    MILLILITERS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_mililiters, quantity)
        }
    },
    LITERS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_liters, quantity)
        }
    },
    CUPS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_cups, quantity)
        }
    },
    TABLESPOONS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_tablespoons, quantity)
        }
    },
    TEASPOONS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_teaspoons, quantity)
        }
    },
    PIECES {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_pieces, quantity)
        }
    },
    SERVINGS {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_servings, quantity)
        }
    },
    CALORIES {
        override fun getPluralLabel(context: Context, quantity: Int): String {
            return context.resources.getQuantityString(R.plurals.enum_unity_calories, quantity)
        }
    },
    PERCENTAGE {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_unity_percentage)
        }
    }
}
