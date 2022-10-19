package com.example.version2.presentation.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version2.domain.model.User
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.SignUpUseCase
import com.example.version2.domain.usecase.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class SignUpViewModel(
    private val userRepository: UserRepository,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {


   lateinit var userData: User
    var imageFileName: String? = null
    var userID: Int = 0


    suspend fun createAccount(user: User): Long {
        val res = viewModelScope.async(Dispatchers.IO) {

            return@async userRepository.createUser(user, 0)
        }

        return res.await()
    }




    suspend fun checkEmailExist(email: String): ValidationResult {

        return signUpUseCase.checkEmailExist(email)

    }

    fun validateMobileNumber(mobile: String): ValidationResult {
        return signUpUseCase.validateMobileNumber(mobile)

    }


    fun validatePinCode(pincode: String): ValidationResult {
        return signUpUseCase.validatePincode(pincode)
    }

    fun validatePassword(password:String,confirmPassword:String):ValidationResult
    {
        return signUpUseCase.validatePassword(password,confirmPassword)
    }
}