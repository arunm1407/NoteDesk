package com.example.version2.domain.repository

import androidx.lifecycle.LiveData
import com.example.version2.domain.model.Note


interface NoteRepository {


    suspend fun insert(note: Note, userId: Int): Long
    suspend fun update(note: Note, userId: Int)
    suspend fun delete(id: Int, userId: Int)
    suspend fun getAllNotes(userId: Int): LiveData<List<Note>>


}