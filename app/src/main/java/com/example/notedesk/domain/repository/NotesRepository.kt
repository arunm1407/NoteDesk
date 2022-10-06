package com.example.notedesk.domain.repository

import androidx.lifecycle.LiveData
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.data.data_source.History
import com.example.notedesk.data.data_source.User
import com.example.notedesk.domain.model.Note

interface NotesRepository {


    suspend fun insert(note: Note): Long
    suspend fun delete(id: Int, userId: Int)
    suspend fun update(note: Note)
    suspend fun insertFileName(fileName: FileName): Long
    suspend fun getFileName(noteId: Int, userId: Int): List<String>
    suspend fun deleteFileName(fileName: String)
    suspend fun getAllNotes(userId: Int): LiveData<List<Note>>
    suspend fun deleteFile(id: Int,userId: Int)
    suspend fun insertHistory(history: History)
    suspend fun getHistory(userId: Int): List<String>
    suspend fun deleteHistory(name: String,userId: Int)
    suspend fun isExistingEmail(name: String): Int
    suspend fun createUser(user: User)
    suspend fun validatePassword(email: String, password: String): Int
    suspend fun getUserId(emailId: String): Long
    suspend fun getUser(userId: Long): User
    suspend fun updateUser(user: User)

}