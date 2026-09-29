package com.example.shopping.signup

object SignupValidator {

    private val emailRegex = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "Email is required"
        }

        if (!emailRegex.matches(email)) {
            return "Enter a valid email address"
        }

        return null
    }

    fun getPasswordStrength(
        password: String
    ): PasswordStrength {

        if (password.isEmpty()) {
            return PasswordStrength.NONE
        }

        var score = 0

        if (password.length >= 8) {
            score++
        }

        if (password.any { it.isUpperCase() }) {
            score++
        }

        if (password.any { it.isLowerCase() }) {
            score++
        }

        if (password.any { it.isDigit() }) {
            score++
        }

        if (password.any { !it.isLetterOrDigit() }) {
            score++
        }

        return when {
            score <= 2 -> PasswordStrength.WEAK
            score <= 4 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }
}