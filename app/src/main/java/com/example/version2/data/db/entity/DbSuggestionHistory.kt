package com.example.version2.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity
data class DbSuggestionHistory(
    val userId:Int=0,
    val timeStamp: Long,
    val suggestion: String,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0


)
