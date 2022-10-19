package com.example.version2.data.db.respositoryImplmentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.version2.data.db.dao.NotesDao
import com.example.version2.data.db.mapper.NotesEntityMapperImpl
import com.example.version2.domain.model.Note
import com.example.version2.domain.repository.NoteRepository


class NotesRepositoryImplementation(private val notesDao: NotesDao) : NoteRepository {
    override suspend fun insert(note: Note, userId: Int): Long {
        return notesDao.insert(NotesEntityMapperImpl.toEntity(note,userId))
    }

    override suspend fun update(note: Note, userId: Int) {
        notesDao.update(NotesEntityMapperImpl.toEntity(note,userId))
    }

    override suspend fun delete(id: Int, userId: Int) {
        notesDao.delete(id, userId)
    }


    override suspend fun getAllNotes(userId: Int): LiveData<List<Note>> {
        return Transformations.map(notesDao.getAllNotes(userId)) { list ->
            list.map {
                NotesEntityMapperImpl.fromEntity(it)
            }
        }

    }


}







