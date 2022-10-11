package com.example.notedesk.presentation.onBoarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.NoteDataBase
import com.example.notedesk.data.repository.NotesRepositoryImplemenation
import com.example.notedesk.data.repository.UserRepositoryImplementation
import com.example.notedesk.domain.repository.NotesRepository
import com.example.notedesk.domain.repository.UserRepository
import kotlinx.coroutines.launch

 class OnBoardingViewModel(application: Application) : AndroidViewModel(application) {


    private val repo: UserRepository


    init {
        val dao = NoteDataBase.getDatabase(application).getUserDao()
        repo = UserRepositoryImplementation(dao)
    }



    fun setOnBoarded(userId: Int) {

        viewModelScope.launch {

            repo.setOnBoardedStatus(true, userId)


        }
    }
}