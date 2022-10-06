package com.example.notedesk.presentation.activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.NoteDataBase
import com.example.notedesk.data.repository.NotesRepositoryImplemenation
import com.example.notedesk.domain.model.Note
import com.example.notedesk.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


abstract class NotesViewModel(application: Application) : AndroidViewModel(application) {


    protected val repo: NotesRepository



    init {
        Log.i("natz","repo called")
        val dao = NoteDataBase.getDatabase(application).getNotesDao()
        repo = NotesRepositoryImplemenation(dao)

    }


    fun deleteNote(id: Int,userId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(id,userId)

    }

    suspend fun getNotes(userId:Int): LiveData<List<Note>> {
       return repo.getAllNotes(userId)
    }


    suspend fun addNotes(note: Note): Int {
        val res = viewModelScope.async(Dispatchers.IO)
        {
            return@async repo.insert(note)
        }
        return res.await().toInt()


    }


    suspend fun getFileName(noteId: Int,userId: Int): List<String> {
        Log.i("file","file name $noteId  $userId")
        return repo.getFileName(noteId,userId)

    }


}