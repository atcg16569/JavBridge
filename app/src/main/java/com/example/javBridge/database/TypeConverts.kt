package com.example.javBridge.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class MovieTypeConverts {
    @TypeConverter
    fun toDate(dateString: String?): LocalDate? {
        return if (dateString == null) {
            null
        } else {
            LocalDate.parse(dateString);
        }
    }

    @TypeConverter
    fun fromDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toSet(value: String?): MutableSet<String>? {
        return if (value == null) {
            null
        } else {
            Json.decodeFromString(value)
        }
    }

    @TypeConverter
    fun fromSet(set: MutableSet<String>?): String? {
        return if (set == null) {
            null
        } else {
            Json.encodeToString(set)
        }
    }

}