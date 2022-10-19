package com.example.version2.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.version2.domain.model.Attachment
import com.example.version2.domain.model.Color
import com.example.version2.domain.model.Priority


@Entity
data class DbNotes(

    val title: String,
    val subtitle: String,
    val createdTime: Long,
    val modifiedTime: Long,
    val noteText: String,
    val color: String = Color.Color1.color,
    val weblink: ArrayList<String> = ArrayList(),
    val priority: Priority = Priority.LOW,
    val favorite: Boolean = false,
    val attachment: ArrayList<Attachment> = ArrayList(),
    val userID: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0


)
