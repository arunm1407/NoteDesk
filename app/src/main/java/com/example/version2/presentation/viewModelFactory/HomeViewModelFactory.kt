package com.example.version2.presentation.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.HomeUseCase
import com.example.version2.presentation.homeScreen.HomeViewModel


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val noteRepository: NoteRepository,private val userRepository: UserRepository,private val homeUseCase: HomeUseCase) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
        {
            return HomeViewModel(noteRepository,userRepository,homeUseCase) as T
        }
        throw IllegalAccessException("Cannot able to create HomeViewModel ")
    }
}