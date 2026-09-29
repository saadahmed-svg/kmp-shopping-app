
//This is the single source of truth for the screen.

//React Native mental model:

//const [name, setName] = useState("")
//const [email, setEmail] = useState("")
//const [password, setPassword] = useState("")
//const [emailError, setEmailError] = useState(...)

package com.example.shopping.signup

data class SignupUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",

    val isEmailTouched: Boolean = false,
    val isPasswordTouched: Boolean = false,

    val emailError: String? = null,

    val passwordStrength: PasswordStrength =
        PasswordStrength.NONE,

    val isPasswordVisible: Boolean = false,

    val isSubmitting: Boolean = false,

    val isSignupSuccessful: Boolean = false
) {
    val isEmailValid: Boolean
        get() = email.isNotBlank() && emailError == null

    val isPasswordValid: Boolean
        get() = passwordStrength != PasswordStrength.WEAK &&
                passwordStrength != PasswordStrength.NONE

    val canSubmit: Boolean
        get() = isEmailValid && isPasswordValid
}