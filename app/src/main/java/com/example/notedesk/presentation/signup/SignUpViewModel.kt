package com.example.notedesk.presentation.signup

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.User
import com.example.notedesk.presentation.activity.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class SignUpViewModel(application: Application) : NotesViewModel(application) {

    val userData: User = User()
    var imageFileName: String? = null
    var userID:Int=0


   suspend fun isExistingEmail(name: String): Boolean {
        if (repo.isExistingEmail(name) == 1)
            return true
        return false
    }

    suspend fun createAccount(user: User):Long
    {
        val res=viewModelScope.async (Dispatchers.IO) {

            return@async repo.createUser(user)
        }

        return res.await()
    }


    suspend fun checkUserIsOnboarded(userid:Int):Boolean
    {
        return repo.getUser(userid.toLong()).isOnBoarded
    }





}