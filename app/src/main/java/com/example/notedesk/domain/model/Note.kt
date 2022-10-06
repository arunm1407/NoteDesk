package com.example.notedesk.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.example.notedesk.util.keys.Keys



data class Note(

    var title: String = "",


    var subtitle: String = "",

    var createdTime: Long = 0,

    var modifiedTime: Long = 0,

    var noteText: String = "",


    var color: String = Keys.COLOR_1,

    var weblink: ArrayList<String> = ArrayList(),

    var priority: Int = Keys.GREEN,

    var attachmentCount: Int = 0,


    var favorite: Boolean = false,

    var userID: Int = 0,


    var id: Int = 0


) :  Parcelable {
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
        parcel.writeInt(priority)
        parcel.writeInt(attachmentCount)
        parcel.writeByte(if (favorite) 1 else 0)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }








}