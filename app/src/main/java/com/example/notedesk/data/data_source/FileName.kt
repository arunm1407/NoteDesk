package com.example.notedesk.data.data_source

import androidx.room.*


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Notes::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE

        )
    ],
    indices = [Index(value = ["name"], unique = true)]

)
data class FileName(

    val name: String,
    val noteId: Int,
    val userId:Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0

)