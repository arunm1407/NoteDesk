package com.example.notedesk.domain.usecase

import android.util.Patterns
import com.example.notedesk.presentation.signup.SignUpViewModel

object ValidateEmail {

    suspend fun execute(email: String, viewModel: SignUpViewModel): ValidationResult {

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }

        if(viewModel.isExistingEmail(email)) {
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