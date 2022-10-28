package com.example.version2.presentation.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.version2.domain.repository.UserRepository
import com.example.version2.presentation.signUp.SignUpUseCaseWrapper
import com.example.version2.presentation.signUp.SignUpViewModel

@Suppress("UNCHECKED_CAST")
class SignUpViewModelFactory(
    private val userRepository: UserRepository,
    private val signUpUseCaseWrapper: SignUpUseCaseWrapper
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(userRepository, signUpUseCaseWrapper) as T
        }
        throw IllegalAccessException("Cannot able to create SignUpViewModel ")
    }
}