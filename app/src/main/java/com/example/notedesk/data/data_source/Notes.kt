package com.example.notedesk.data.data_source


import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.notedesk.domain.util.keys.IndentKeys
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



@Entity
data class Notes(


    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "subtitle")
    var subtitle: String = "",

    @ColumnInfo(name = "CreatedTime")
    var createdTime: Long = 0,

    @ColumnInfo(name = "ModifiedTime")
    var modifiedTime: Long = 0,

    @ColumnInfo(name = "note_Text")
    var noteText: String = "",


    @ColumnInfo(name = "color")
    var color: String = "",

    @ColumnInfo(name = "web_link")
    var weblink: ArrayList<String> = ArrayList(),

    @ColumnInfo(name = "priority")
    var priority: Int = IndentKeys.GREEN,
    @ColumnInfo(name = "attachmentCount")
    var attachmentCount: Int = 0,

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,


    @PrimaryKey(autoGenerate = true)
    val id: Int = 0


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeLong(createdTime)
        parcel.writeLong(modifiedTime)
        parcel.writeString(noteText)
        parcel.writeString(color)
        parcel.writeList(weblink)
        parcel.writeInt(priority)
        parcel.writeInt(attachmentCount)
        parcel.writeByte(if (favorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notes> {
        override fun createFromParcel(parcel: Parcel): Notes {
            return Notes(parcel)
        }

        override fun newArray(size: Int): Array<Notes?> {
            return arrayOfNulls(size)
        }
    }


    class StringConvertor {
        @TypeConverter
        fun fromString(value: String?): ArrayList<String> {
            val listType = object : TypeToken<ArrayList<String>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun fromArrayList(list: ArrayList<String>): String {
            return Gson().toJson(list)
        }
    }
}











