package com.example.version2.domain.usecase

import com.example.version2.domain.repository.UserRepository


class CheckUserAuthentication(val userRepository: UserRepository) {

    suspend  operator fun invoke(
        email: String,
        password: String
    ): ValidationResult {

        if (!userRepository.validatePassword(email, password)) {
            return ValidationResult(
                successful = false,
                errorMessage = " Password do not Match "
            )
        }

        return ValidationResult(
            successful = true
        )
    }


}