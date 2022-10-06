package com.example.notedesk.data.data_source

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class History(
    val userId:Int=0,
    val timeStamp: Long =0,
    val suggestion: String,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0


)
