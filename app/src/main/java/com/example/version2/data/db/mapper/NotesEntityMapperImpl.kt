package com.example.version2.data.db.mapper

import com.example.version2.data.db.entity.DbNotes
import com.example.version2.domain.model.Color.Companion.getValues
import com.example.version2.domain.model.Note

object NotesEntityMapperImpl  : NoteEntityMapper<DbNotes> {
    override fun fromEntity(entity: DbNotes): Note {
        return Note(
            entity.title,
            entity.subtitle,
            entity.createdTime,
            entity.modifiedTime,
            entity.noteText,
            entity.color.getValues(),
            entity.weblink,
            entity.priority,
            entity.favorite,
            entity.attachment,
            entity.id

        )
    }


    override fun toEntity(model: Note, userId: Int): DbNotes {
        return DbNotes(
            model.title,
            model.subtitle,
            model.createdTime,
            model.modifiedTime,
            model.noteText,
            model.color.color,
            model.weblink,
            model.priority,
            model.favorite,
            model.attachments,
            userId,
            model.id
        )
    }
}