package com.example.version2.presentation.profile

import com.example.version2.domain.usecase.CheckField
import com.example.version2.domain.usecase.ValidateMobileNumber
import com.example.version2.domain.usecase.ValidatePinCode
import com.example.version2.domain.usecase.ValidateString

data class FormUseCaseWrapper(
    val checkField: CheckField,
    val validateMobileNumber: ValidateMobileNumber,
    val validatePinCode: ValidatePinCode,
    val validateString: ValidateString


)