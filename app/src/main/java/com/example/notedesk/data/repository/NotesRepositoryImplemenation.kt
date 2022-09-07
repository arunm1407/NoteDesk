package com.example.notedesk.data.repository

import androidx.lifecycle.LiveData
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.data.data_source.NoteDao
import com.example.notedesk.domain.repository.NotesRepository

class NotesRepositoryImplemenation(private val notesDao: NoteDao): NotesRepository {

    private val allNotes: LiveData<List<Notes>> = notesDao.getAllNotes()
    override fun getAllNotes(): LiveData<List<Notes>> {
        return allNotes
    }

    override suspend fun deleteFile(id: Int) {
        notesDao.deleteFile(id)
    }


    override suspend fun insert(note: Notes):Long {
        return notesDao.insert(note)
    }

    override suspend fun delete(id: Int) {
        notesDao.delete(id)
    }

    override suspend fun update(note: Notes) {
        notesDao.update(note)
    }

    override suspend fun insertFileName(fileName: FileName):Long {
         return notesDao.insertFileName(fileName)
    }
     override suspend fun getFileName(noteId: Int):MutableList<String>
    {
        return notesDao.getFileName(noteId)
    }

    override suspend fun deleteFileName(fileName: String) {
        notesDao.deleteFileName(fileName)
    }




}
