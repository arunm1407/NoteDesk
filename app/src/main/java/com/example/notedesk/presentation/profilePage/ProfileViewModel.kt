package com.example.notedesk.presentation.profilePage

import android.app.Application
import com.example.notedesk.data.data_source.User
import com.example.notedesk.presentation.activity.NotesViewModel


class ProfileViewModel(application: Application) : NotesViewModel(application) {
    lateinit var user: User
    var oldImagePath: String? = null


    suspend fun updateData(user: User) {
        repo.updateUser(user)
    }
}