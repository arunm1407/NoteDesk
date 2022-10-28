package com.example.version2.presentation.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.presentation.previewNote.PreviewViewModel


@Suppress("UNCHECKED_CAST")
class PreviewNoteViewModelFactory(private val noteRepository: NoteRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreviewViewModel::class.java))
        {
            return PreviewViewModel(noteRepository) as T
        }
        throw IllegalAccessException("Cannot able to create PreviewViewModel ")
    }
}