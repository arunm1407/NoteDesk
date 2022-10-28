package com.example.version2.presentation.signUp

import com.example.version2.domain.usecase.*

data class SignUpUseCaseWrapper(
    val checkEmailExist: CheckEmailExist,
    val validateMobileNumber: ValidateMobileNumber,
    val validatePincode: ValidatePinCode,
    val validatePassword: ValidatePassword,
    val validateString: ValidateString
)
