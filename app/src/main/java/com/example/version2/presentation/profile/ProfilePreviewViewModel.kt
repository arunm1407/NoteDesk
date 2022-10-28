package com.example.version2.presentation.profile

import com.example.version2.domain.model.User
import com.example.version2.domain.repository.UserRepository
import com.example.version2.presentation.login.LoginUseCaseWrapper
import com.example.version2.presentation.login.LoginViewModel

class ProfilePreviewViewModel(userRepository: UserRepository, loginUseCaseWrapper: LoginUseCaseWrapper) : LoginViewModel(userRepository,loginUseCaseWrapper) {


    lateinit var user: User

    suspend fun getUser(userId: Int): User {
        return userRepository.getUser(userId.toLong())
    }

}