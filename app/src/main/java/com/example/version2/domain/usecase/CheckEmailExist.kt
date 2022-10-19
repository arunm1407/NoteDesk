package com.example.version2.domain.usecase

import android.util.Patterns
import com.example.version2.domain.repository.UserRepository

class CheckEmailExist(private val userRepository: UserRepository) {

    suspend operator fun invoke(email: String): ValidationResult {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }

        if (userRepository.isExistingEmail(email)) {
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