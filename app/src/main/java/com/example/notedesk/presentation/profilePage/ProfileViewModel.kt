package com.example.notedesk.presentation.profilePage

import android.app.Application
import com.example.notedesk.data.data_source.User
import com.example.notedesk.presentation.activity.NotesViewModel



class ProfileViewModel(application: Application) : NotesViewModel(application) {
     var userId: Int=0
     var user: User= User()
    var oldImagePath: String? = null


    suspend fun getUser(userId:Int):User
    {
       return repo.getUser(userId.toLong())
    }



}
