package com.example.version2.presentation.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.SignUpUseCase
import com.example.version2.presentation.signUp.SignUpViewModel

@Suppress("UNCHECKED_CAST")
class SignUpViewModelFactory(
    private val userRepository: UserRepository,
    private val signUpUseCase: SignUpUseCase
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(userRepository, signUpUseCase) as T
        }
        throw IllegalAccessException("Cannot able to create SignUpViewModel ")
    }
}