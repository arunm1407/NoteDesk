package com.example.version2.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version2.domain.usecase.ValidationResult
import com.example.version2.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class LoginViewModel(
    val userRepository: UserRepository,
    private val loginUseCaseWrapper: LoginUseCaseWrapper
) : ViewModel() {

    private var _userId: Int = 0


    val userId: Int
        get() = _userId


    fun setUserId(id: Int) {
        _userId = id
    }


    suspend fun getUserIDFromEmail(email: String): Int {
        return if (userRepository.getUserId(email) == null) 0 else userRepository.getUserId(email)!!
            .toInt()
    }


    suspend fun checkUserIsOnBoarded(userId: Int): Boolean {
        return userRepository.getUser(userId.toLong()).isOnBoarded
    }

    suspend fun getOnBoardedStatus(userId: Int): Boolean {
        return userRepository.getOnBoardedStatus(userId)
    }

    fun setOnBoardedStatus(status: Boolean, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.setOnBoardedStatus(status, userId)
        }

    }

    suspend fun validateEmail(email: String): ValidationResult {
        return loginUseCaseWrapper.validateEmail.execute(email)
    }


    suspend fun checkUserAuth(email: String, password: String): ValidationResult {
        return loginUseCaseWrapper.checkAuth.invoke(email, password)
    }
}