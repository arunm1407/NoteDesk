package com.example.version2.presentation.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version2.domain.model.Gender
import com.example.version2.domain.model.User
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class SignUpViewModel(
    private val userRepository: UserRepository,
    private val signUpUseCaseWrapper: SignUpUseCaseWrapper

) : ViewModel() {


    lateinit var userData: User
    var imageFileName: String? = null
    var userID: Int = 0
lateinit var gender: Gender

    suspend fun createAccount(user: User): Long {
        val res = viewModelScope.async(Dispatchers.IO) {

            return@async userRepository.createUser(user, 0)
        }

        return res.await()
    }


    suspend fun checkEmailExist(email: String): ValidationResult {

        return signUpUseCaseWrapper.checkEmailExist(email)

    }

    fun validateMobileNumber(mobile: String): ValidationResult {
        return signUpUseCaseWrapper.validateMobileNumber(mobile)

    }


    fun validatePinCode(pincode: String): ValidationResult {
        return signUpUseCaseWrapper.validatePincode(pincode)
    }

    fun validatePassword(password: String, confirmPassword: String): ValidationResult {
        return signUpUseCaseWrapper.validatePassword(password, confirmPassword)
    }


    fun validateString(field:String,max:Int,fieldName:String):ValidationResult
    {
        return signUpUseCaseWrapper.validateString(field,max,fieldName)
    }
}