package com.example.notedesk.data.data_source


import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.notedesk.domain.util.keys.Keys
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
    var priority: Int = Keys.GREEN,
    @ColumnInfo(name = "attachmentCount")
    var attachmentCount: Int = 0,

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,


    @PrimaryKey(autoGenerate = true)
    val id: Int = 0


) : Parcelable, NotesRvItem() {
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


    override fun equals(other: Any?): Boolean {


        if (this === other) return true
        if (other !is Notes) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (color != other.color) return false
        if (createdTime != other.createdTime) return false
        if (modifiedTime != other.modifiedTime) return false
        if (favorite != other.favorite) return false
        if (attachmentCount != other.attachmentCount) return false
        if (noteText != other.noteText) return false
        if (priority != other.priority) return false
        if (weblink != other.weblink) return false

        return true

    }
}












