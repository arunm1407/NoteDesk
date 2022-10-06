package com.example.notedesk.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.notedesk.data.data_source.*
import com.example.notedesk.data.util.fromDomain
import com.example.notedesk.data.util.toDomain
import com.example.notedesk.domain.model.Note
import com.example.notedesk.domain.repository.NotesRepository

class NotesRepositoryImplemenation(private val notesDao: NoteDao) : NotesRepository {



    override suspend fun getAllNotes(userId:Int): LiveData<List<Note>> {
        return Transformations.map(notesDao.getAllNotes(userId)) { list ->

            list.map { Notes ->
                Notes.toDomain()
            }
        }

    }


    override suspend fun deleteFile(id: Int,userId: Int) {
        notesDao.deleteFile(id,userId)
    }

    override suspend fun insertHistory(history: History) {
        notesDao.insertHistory(history)
    }

    override suspend fun getHistory(userId: Int): List<String> {
        return notesDao.getHistory(userId)
    }

    override suspend fun deleteHistory(name: String,userId: Int) {
        notesDao.deleteHistory(name,userId)
    }

    override suspend fun isExistingEmail(name: String):Int {
       return notesDao.isExistingEmail(name)
    }

    override suspend fun createUser(user: User) {
        notesDao.createUser(user)
    }

    override suspend fun validatePassword(email: String, password: String):Int {
        return notesDao.validatePassword(email,password)
    }

    override suspend fun getUserId(emailId: String): Long {
        return notesDao.getUserId(emailId)
    }

    override suspend fun getUser(userId: Long): User {
        return notesDao.getUser(userId)
    }

    override suspend fun updateUser(user: User) {
        notesDao.updateUser(user)
    }


    override suspend fun insert(note: Note): Long {

        return notesDao.insert(note.fromDomain())
    }

    override suspend fun delete(id: Int,userId: Int) {
        notesDao.delete(id,userId)
    }

    override suspend fun update(note: Note) {
        notesDao.update(note.fromDomain())
    }

    override suspend fun insertFileName(fileName: FileName): Long {
        return notesDao.insertFileName(fileName)
    }

    override suspend fun getFileName(noteId: Int,userId: Int): List<String> {
        return notesDao.getFileName(noteId,userId)
    }

    override suspend fun deleteFileName(fileName: String) {
        notesDao.deleteFileName(fileName)
    }


}