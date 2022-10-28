package com.example.version2.domain.usecase

import androidx.core.text.isDigitsOnly

class ValidateMobileNumber {

    operator fun invoke(mobile: String): ValidationResult {


        if (!mobileCheck(mobile)) {
            return ValidationResult.Error("The mobile number is invalid")
        }
        return ValidationResult.Successful

    }


    private fun mobileCheck(mobile: String): Boolean {

        if (!mobile.isDigitsOnly() || mobile.length != 10 || mobile.first().toString()
                .toInt() !in listOf(6, 7, 8, 9)
        ) {

            return false
        }
        return true
    }


}