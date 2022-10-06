package com.example.notedesk.domain.usecase

object ValidatePassword {



    fun execute(pass1: String,pass2: String): ValidationResult {
        if(pass1!=pass2) {
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