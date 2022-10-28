package com.example.version2.presentation.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.version2.domain.repository.UserRepository
import com.example.version2.presentation.login.LoginUseCaseWrapper
import com.example.version2.presentation.profile.ProfilePreviewViewModel


@Suppress("UNCHECKED_CAST")
class ProfilePreviewViewModelFactory(
    private val userRepository: UserRepository,
    private val loginUseCaseWrapper: LoginUseCaseWrapper
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfilePreviewViewModel::class.java)) {
            return ProfilePreviewViewModel(
                userRepository, loginUseCaseWrapper
            ) as T
        }
        throw IllegalAccessException("Cannot able to create ProfilePreviewViewModel ")
    }
}