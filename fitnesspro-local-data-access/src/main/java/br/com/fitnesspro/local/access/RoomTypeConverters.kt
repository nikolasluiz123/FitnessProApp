package br.com.fitnesspro.local.access

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

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
}

