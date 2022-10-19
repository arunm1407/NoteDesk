package com.example.version2.presentation.common


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version2.domain.model.Note
import com.example.version2.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class NotesViewModel(val noteRepository: NoteRepository) : ViewModel() {


    fun deleteNote(id: Int, userId: Int) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.delete(id, userId)


    }

    suspend fun getNotes(userId: Int): LiveData<List<Note>> {
        return noteRepository.getAllNotes(userId)
    }


    suspend fun addNotes(note: Note, userId: Int): Int {
        val res = viewModelScope.async(Dispatchers.IO)
        {
            return@async noteRepository.insert(note, userId)
        }
        return res.await().toInt()


    }


}