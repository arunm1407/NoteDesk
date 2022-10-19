package com.example.version2.domain.model


import java.io.Serializable


data class Note(
    val title: String,
    val subtitle: String,
    val createdTime: Long,
    val modifiedTime: Long,
    val noteText: String,
    val color: Color,
    val weblink: ArrayList<String> = ArrayList(),
    val priority: Priority = Priority.LOW,
    val favorite: Boolean =false,
    val attachments: ArrayList<Attachment> =ArrayList(),
    val id:Int=0,
):Serializable


