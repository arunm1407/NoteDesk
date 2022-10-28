package com.example.version2.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version2.domain.model.Gender
import com.example.version2.domain.model.User
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val formUseCaseWrapper: FormUseCaseWrapper
) : ViewModel() {


    private var _image: String? = null

   lateinit var gender: Gender
    val image: String?
        get() = _image

    fun setImage(name: String?) {
        _image = name
    }

    fun updateData(user: User, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUser(user, userId)
        }

    }


    lateinit var user: User


    fun validateField(field: String, name: String): ValidationResult {
        return formUseCaseWrapper.checkField.invoke(field, name)
    }

    fun validateMobileNumber(mobile: String): ValidationResult {
        return formUseCaseWrapper.validateMobileNumber(mobile)
    }


    fun validatePinCode(pincode: String): ValidationResult {
        return formUseCaseWrapper.validatePinCode(pincode)
    }

    fun validateString(field:String,max:Int,fieldName:String):ValidationResult
    {
        return formUseCaseWrapper.validateString(field,max,fieldName)
    }


}