package com.example.notedesk.presentation.profilePage

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.User
import com.example.notedesk.presentation.activity.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditNoteViewModel(application: Application) : NotesViewModel(application) {
    fun updateData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateUser(user)
        }

    }

    var userId: Int = 0
    lateinit var user: User
}