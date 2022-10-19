package com.example.version2.domain.usecase

import android.util.Patterns
import com.example.version2.domain.repository.UserRepository


class ValidateNewEmail(private val userRepository: UserRepository) {


    suspend fun execute(
        email: String
    ): ValidationResult {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }


        if (!userRepository.isExistingEmail(email)) {
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