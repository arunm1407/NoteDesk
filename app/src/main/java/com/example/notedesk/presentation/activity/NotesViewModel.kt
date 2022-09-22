package com.example.notedesk.presentation.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.NoteDataBase
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.data.repository.NotesRepositoryImplemenation
import com.example.notedesk.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


abstract class NotesViewModel(application: Application) : AndroidViewModel(application) {


    protected val repo: NotesRepository


    init {
        val dao = NoteDataBase.getDatabase(application).getNotesDao()
        repo = NotesRepositoryImplemenation(dao)


    }


    fun deleteNote(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(id)

    }

    fun getNotes(): LiveData<List<Notes>> = repo.getAllNotes()


    suspend fun addNotes(note: Notes): Int {
        val res = viewModelScope.async(Dispatchers.IO)
        {
            return@async repo.insert(note)
        }
        return res.await().toInt()


    }


      fun getFileName(noteId: Int): MutableList<String> {
        return repo.getFileName(noteId)

    }




}