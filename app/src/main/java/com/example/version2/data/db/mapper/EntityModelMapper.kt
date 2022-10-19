package com.example.version2.data.db.mapper

import com.example.version2.domain.model.Note
import com.example.version2.domain.model.SuggestionHistory
import com.example.version2.domain.model.User


interface EntityModelMapper<T, U> {
    fun fromEntity(entity: T): U
    fun toEntity(model: U,userId:Int): T
}

interface UserEntityMapper<T> : EntityModelMapper<T, User>

interface SuggestionEntityMapper<T> : EntityModelMapper<T, SuggestionHistory>

interface NoteEntityMapper<T> : EntityModelMapper<T, Note>