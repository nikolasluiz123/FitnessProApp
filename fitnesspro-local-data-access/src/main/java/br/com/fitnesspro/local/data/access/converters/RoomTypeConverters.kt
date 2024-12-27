package br.com.fitnesspro.local.data.access.converters

import androidx.room.TypeConverter
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.extensions.parseToLocalDateTime
import br.com.fitnesspro.core.extensions.parseToLocalTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class RoomTypeConverters {
    @TypeConverter
    fun toDateTime(dateString: String?): LocalDateTime? {
        return dateString?.parseToLocalDateTime(EnumDateTimePatterns.DATE_TIME_SQLITE)
    }

    @TypeConverter
    fun toDateTimeString(date: LocalDateTime?): String? {
        return date?.format(EnumDateTimePatterns.DATE_TIME_SQLITE)
    }

    @TypeConverter
    fun toDate(dateString: String?): LocalDate? {
        return dateString?.parseToLocalDate(EnumDateTimePatterns.DATE_SQLITE)
    }

    @TypeConverter
    fun toDateString(date: LocalDate?): String? {
        return date?.format(EnumDateTimePatterns.DATE_SQLITE)
    }

    @TypeConverter
    fun toTime(timeString: String?): LocalTime? {
        return timeString?.parseToLocalTime(EnumDateTimePatterns.TIME)
    }

    @TypeConverter
    fun toTimeString(time: LocalTime?): String? {
        return time?.format(EnumDateTimePatterns.TIME)
    }
}

