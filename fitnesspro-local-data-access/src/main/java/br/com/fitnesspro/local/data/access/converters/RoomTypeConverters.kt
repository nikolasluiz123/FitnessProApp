package br.com.fitnesspro.local.data.access.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class RoomTypeConverters {
    @TypeConverter
    fun toDateTime(dateString: String?): LocalDateTime? {
        return dateString?.let {
            LocalDateTime.parse(it)
        }
    }

    @TypeConverter
    fun toDateTimeString(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toDate(dateString: String?): LocalDate? {
        return dateString?.let {
            LocalDate.parse(it)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toTime(timeString: String?): LocalTime? {
        return timeString?.let {
            LocalTime.parse(it)
        }
    }

    @TypeConverter
    fun toTimeString(time: LocalTime?): String? {
        return time?.toString()
    }
}

