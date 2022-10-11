package com.example.notedesk.domain.usecase



object CheckUserAuthentication {

    suspend fun execute(
        email: String,
        password: String,
        validateUser: suspend (email: String, password: String) -> Boolean
    ): ValidationResult {

        if (!validateUser(email, password)) {
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