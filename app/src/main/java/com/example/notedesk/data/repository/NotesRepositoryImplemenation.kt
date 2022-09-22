package com.example.notedesk.data.repository

import androidx.lifecycle.LiveData
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.data.data_source.History
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.data.data_source.NoteDao
import com.example.notedesk.domain.repository.NotesRepository

class NotesRepositoryImplemenation(private val notesDao: NoteDao) : NotesRepository {

    private val allNotes: LiveData<List<Notes>> = notesDao.getAllNotes()




    override fun getAllNotes(): LiveData<List<Notes>> {
        return allNotes
    }



    override suspend fun deleteFile(id: Int) {
        notesDao.deleteFile(id)
    }

    override suspend fun insertHistory(history: History) {
        notesDao.insertHistory(history)
    }

    override  fun getHistory(): List<String> {
        return notesDao.getHistory()
    }

    override  fun getSearchNotes(): List<Notes> {
        return notesDao.getNotesForSearch()
    }

    override suspend fun deleteHistory(name: String) {
        notesDao.deleteHistory(name)
    }


    override suspend fun insert(note: Notes): Long {
        return notesDao.insert(note)
    }

    override suspend fun delete(id: Int) {
        notesDao.delete(id)
    }

    override suspend fun update(note: Notes) {
        notesDao.update(note)
    }

    override suspend fun insertFileName(fileName: FileName): Long {
        return notesDao.insertFileName(fileName)
    }

    override  fun getFileName(noteId: Int): MutableList<String> {
        return notesDao.getFileName(noteId)
    }

    override suspend fun deleteFileName(fileName: String) {
        notesDao.deleteFileName(fileName)
    }


}
