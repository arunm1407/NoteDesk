package com.example.notedesk.presentation.signup

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.User
import com.example.notedesk.presentation.activity.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUpViewModel(application: Application) : NotesViewModel(application) {

    val userData: User = User()
    var imageFileName: String? = null


   suspend fun isExistingEmail(name: String): Boolean {
        if (repo.isExistingEmail(name) == 1)
            return true
        return false
    }

    fun createAccount(user: User)
    {
        viewModelScope.launch(Dispatchers.IO) {

            repo.createUser(user)
        }
    }







}