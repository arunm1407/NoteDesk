package com.example.version2.domain.usecase


class ValidatePassword {


    operator fun invoke(pass1: String, pass2: String): ValidationResult {
        if (pass1 != pass2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The Password does not match"
            )
        }
        return ValidationResult(
            successful = true
        )
    }

}