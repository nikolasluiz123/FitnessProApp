package br.com.fitnesspro.model.enums

import android.content.Context
import br.com.fitnesspro.model.enums.interfaces.IEnumDomain
import br.com.fitnesspro_model.R

enum class EnumUserType : IEnumDomain {
    PERSONAL_TRAINER {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_user_type_personal_trainer)
        }
    },
    NUTRITIONIST {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_user_type_nutritionist)
        }
    },
    ACADEMY_MEMBER {
        override fun getLabel(context: Context): String {
            return context.getString(R.string.enum_user_type_academy_member)
        }
    }
}
