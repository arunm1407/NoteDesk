package com.example.version2.presentation.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.FormUseCase
import com.example.version2.presentation.profile.EditProfileViewModel


@Suppress("UNCHECKED_CAST")
class EditProfileViewModelFactory(private val userRepository: UserRepository, private val formUseCase: FormUseCase) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java))
        {
            return EditProfileViewModel(userRepository,formUseCase) as T
        }
        throw IllegalAccessException("Cannot able to create EditProfileViewModel ")
    }
}