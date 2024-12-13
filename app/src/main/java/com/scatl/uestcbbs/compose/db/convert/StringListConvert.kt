package com.scatl.uestcbbs.compose.db.convert

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * Created by sca_tl at 2024/8/1 9:59:06
 */
class StringListConvert {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.let { adapter.fromJson(it) }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return adapter.toJson(list)
    }
}