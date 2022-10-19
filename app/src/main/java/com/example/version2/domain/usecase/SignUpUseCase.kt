package com.example.version2.domain.usecase

data class SignUpUseCase(
    val checkEmailExist: CheckEmailExist,
    val validateMobileNumber:ValidateMobileNumber,
    val validatePincode:ValidatePinCode,
    val validatePassword:ValidatePassword
)
