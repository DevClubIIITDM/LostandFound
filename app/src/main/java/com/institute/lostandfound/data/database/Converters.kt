package com.institute.lostandfound.data.database

import androidx.room.TypeConverter
import com.institute.lostandfound.data.model.ItemType
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromItemType(value: ItemType): String {
        return value.name
    }

    @TypeConverter
    fun toItemType(value: String): ItemType {
        return ItemType.valueOf(value)
    }
} 