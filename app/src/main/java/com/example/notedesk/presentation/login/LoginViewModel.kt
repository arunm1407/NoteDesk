package com.example.notedesk.presentation.login

import android.app.Application
import com.example.notedesk.presentation.activity.NotesViewModel

class LoginViewModel(application: Application) : NotesViewModel(application) {

    private var _userId: Int = 0

    val userId:Int
      get() = _userId



    fun setUserId(id:Int)
    {
        _userId=id
    }



    suspend fun isExistingEmail(name: String): Boolean {
        if (repo.isExistingEmail(name) == 1)
            return true
        return false
    }


    suspend fun checkUserAuthentication(name: String, password: String): Boolean {
        if (repo.validatePassword(name, password) == 1)
            return true
        return false

    }


    suspend fun getUserIDFromEmail(email: String): Int {
        return repo.getUserId(email).toInt()
    }


    suspend fun checkUserIsOnBoarded(userId: Int): Boolean {
        return repo.getUser(userId.toLong()).isOnBoarded
    }
}