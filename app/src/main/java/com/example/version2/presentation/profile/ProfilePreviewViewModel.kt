package com.example.version2.presentation.profile

import com.example.version2.domain.model.User
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.LoginUseCase
import com.example.version2.presentation.login.LoginViewModel

class ProfilePreviewViewModel(userRepository: UserRepository,loginUseCase: LoginUseCase) : LoginViewModel(userRepository,loginUseCase) {


    lateinit var user: User

    suspend fun getUser(userId: Int): User {
        return userRepository.getUser(userId.toLong())
    }

}