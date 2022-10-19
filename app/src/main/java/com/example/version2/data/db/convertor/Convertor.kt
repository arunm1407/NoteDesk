package com.example.version2.data.db.convertor

import androidx.room.TypeConverter
import com.example.version2.domain.model.Attachment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convertor {
    @TypeConverter
    fun fromString(value: String?): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromAttachment(value: String?): ArrayList<Attachment> {
        val listType = object : TypeToken<ArrayList<Attachment>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromAttachmentList(list: ArrayList<Attachment>): String {
        return Gson().toJson(list)
    }


}



