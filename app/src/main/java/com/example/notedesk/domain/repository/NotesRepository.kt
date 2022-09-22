package com.example.notedesk.domain.repository

import androidx.lifecycle.LiveData
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.data.data_source.History
import com.example.notedesk.data.data_source.Notes

interface NotesRepository {


    suspend fun insert(note: Notes): Long
    suspend fun delete(id: Int)
    suspend fun update(note: Notes)
    suspend fun insertFileName(fileName: FileName): Long
    fun getFileName(noteId: Int): MutableList<String>
    suspend fun deleteFileName(fileName: String)
    fun getAllNotes(): LiveData<List<Notes>>
    suspend fun deleteFile(id: Int)
    suspend fun insertHistory(history: History)
    fun getHistory(): List<String>
    fun getSearchNotes(): List<Notes>
    suspend fun deleteHistory(name: String)


}