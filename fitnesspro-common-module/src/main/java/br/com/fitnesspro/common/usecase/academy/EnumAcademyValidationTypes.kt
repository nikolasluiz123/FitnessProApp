package br.com.fitnesspro.common.usecase.academy

enum class EnumAcademyValidationTypes {
    REQUIRED_ACADEMY,
    REQUIRED_TIME_START,
    REQUIRED_TIME_END,
    REQUIRED_DAY_OF_WEEK,

    INVALID_TIME_PERIOD,

    CONFLICT_TIME_PERIOD
}