package com.example.notedesk.domain.usecase

import androidx.core.text.isDigitsOnly

object ValidateMobileNumber {

    fun execute(mobile: String): ValidationResult {
        if(!mobileCheck(mobile)) {
            return ValidationResult(
                successful = false,
                errorMessage = "The mobile number is invalid"
            )
        }
        return ValidationResult(
            successful = true
        )
    }





    private fun mobileCheck(mobile:String): Boolean {

        if (!mobile.isDigitsOnly() || mobile.length != 10 || mobile.first().toString()
                .toInt() !in listOf(6, 7, 8, 9)
        ) {

            return false
        }
        return true
    }
}