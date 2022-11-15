package com.roland.android.calculator.data.database

import androidx.room.TypeConverter
import java.util.*

class TypeConverter {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(milliSec: Long?): Date? = milliSec?.let {
        Date(it)
    }
}