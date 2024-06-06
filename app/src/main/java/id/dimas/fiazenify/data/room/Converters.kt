package id.dimas.fiazenify.data.room

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.sql.Date

class Converters {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp): Long {
        return timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000
    }

    @TypeConverter
    fun toTimestamp(value: Long): Timestamp {
        val seconds = value / 1000
        val nanoseconds = (value % 1000) * 1_000_000
        return Timestamp(seconds, nanoseconds.toInt())
    }
}