package com.example.notedesk.presentation.login

import android.app.Application
import android.util.Log
import com.example.notedesk.presentation.activity.NotesViewModel

class LoginViewModel(application: Application) : NotesViewModel(application) {




    suspend fun isExistingEmail(name: String): Boolean {
        if (repo.isExistingEmail(name) == 1)
        return true
        return false
    }



    suspend fun checkUserAuthentication(name: String,password:String):Boolean
    {
        Log.i("anbu","$name   $password")
        if (repo.validatePassword(name, password)==1)
            return true
        return false

    }



    suspend fun getUserIDFromEmail(email:String):Int
    {
        return repo.getUserId(email).toInt()
    }
}