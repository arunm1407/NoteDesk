package com.example.version2.domain.usecase

import androidx.core.text.isDigitsOnly

class ValidatePinCode {


    operator fun invoke(pinCode: String): ValidationResult {

        if (!pinCodeCheck(pinCode)) {
            return ValidationResult(
                successful = false,
                errorMessage = "The pinCode is Invalid"
            )
        }
        return ValidationResult(
            successful = true
        )
    }


    private fun pinCodeCheck(mobile: String): Boolean {

        if (!mobile.isDigitsOnly() || mobile.length != 6) {

            return false
        }
        return true
    }
}