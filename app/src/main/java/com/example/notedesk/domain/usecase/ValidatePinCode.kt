package com.example.notedesk.domain.usecase

import androidx.core.text.isDigitsOnly

object ValidatePinCode {


    fun execute(pinCode: String): ValidationResult {

        if(!pinCodeCheck(pinCode)) {
            return ValidationResult(
                successful = false,
                errorMessage = "The pinCode is Invalid"
            )
        }
        return ValidationResult(
            successful = true
        )
    }



    private fun pinCodeCheck(mobile:String): Boolean {

        if (!mobile.isDigitsOnly() || mobile.length != 6) {

            return false
        }
        return true
    }
}