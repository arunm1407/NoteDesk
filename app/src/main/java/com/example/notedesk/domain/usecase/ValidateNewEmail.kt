package com.example.notedesk.domain.usecase

import android.util.Patterns

object ValidateNewEmail {


    suspend fun execute(
        email: String,
        validateEmail: suspend (email: String) -> Boolean
    ): ValidationResult {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }


        if (!validateEmail(email)) {
            return ValidationResult(
                successful = false,
                errorMessage = "The email does note exist"
            )
        }






        return ValidationResult(
            successful = true
        )
    }
}