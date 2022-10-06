package com.example.notedesk.data.data_source


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notedesk.data.util.Key


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
    var color: String = Key.COLOR_1,

    @ColumnInfo(name = "web_link")
    var weblink: ArrayList<String> = ArrayList(),

    @ColumnInfo(name = "priority")
    var priority: Int = Key.GREEN,
    @ColumnInfo(name = "attachmentCount")
    var attachmentCount: Int = 0,

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,

    @ColumnInfo(name = "userId")
    var userID: Int = 0,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


)





