package com.example.notedesk.domain.usecase

import android.util.Patterns

object ValidateEmail {

    suspend fun execute(email: String,validateEmail  :suspend (email:String)->Boolean): ValidationResult {

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }

        if(validateEmail(email)) {
            return ValidationResult(
                successful = false,
                errorMessage = "The email already exist"
            )
        }
        return ValidationResult(
            successful = true
        )
    }



}