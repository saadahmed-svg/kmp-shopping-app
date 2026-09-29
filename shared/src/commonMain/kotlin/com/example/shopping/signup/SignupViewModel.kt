//the UI doesn't perform validation.

//The ViewModel owns the behavior.


package com.example.shopping.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        SignupUiState()
    )

    val uiState: StateFlow<SignupUiState> =
        _uiState.asStateFlow()

    fun onIntent(intent: SignupIntent) {
        when (intent) {

            is SignupIntent.NameChanged -> {
                _uiState.update {
                    it.copy(
                        name = intent.value
                    )
                }
            }

            is SignupIntent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = intent.value,
                        emailError = if (it.isEmailTouched) {
                            SignupValidator.validateEmail(
                                intent.value
                            )
                        } else {
                            null
                        }
                    )
                }
            }

            is SignupIntent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = intent.value,
                        passwordStrength =
                            SignupValidator.getPasswordStrength(
                                intent.value
                            )
                    )
                }
            }

            SignupIntent.EmailFocusLost -> {
                _uiState.update {
                    it.copy(
                        isEmailTouched = true,
                        emailError =
                            SignupValidator.validateEmail(
                                it.email
                            )
                    )
                }
            }

            SignupIntent.PasswordFocusLost -> {
                _uiState.update {
                    it.copy(
                        isPasswordTouched = true
                    )
                }
            }

            SignupIntent.TogglePasswordVisibility -> {
                _uiState.update {
                    it.copy(
                        isPasswordVisible =
                            !it.isPasswordVisible
                    )
                }
            }

            SignupIntent.Submit -> {
                submit()
            }

            SignupIntent.BackToSignup -> {
                _uiState.update {
                    it.copy(
                        isSignupSuccessful = false
                    )
                }
            }
        }
    }

    private fun submit() {
        val currentState = _uiState.value

        val emailError =
            SignupValidator.validateEmail(
                currentState.email
            )

        val passwordStrength =
            SignupValidator.getPasswordStrength(
                currentState.password
            )

        _uiState.update {
            it.copy(
                isEmailTouched = true,
                isPasswordTouched = true,
                emailError = emailError,
                passwordStrength = passwordStrength
            )
        }

        if (
            emailError != null ||
            passwordStrength == PasswordStrength.NONE ||
            passwordStrength == PasswordStrength.WEAK
        ) {
            return
        }

        // Later: real API call / repository goes here.

        _uiState.update {
            SignupUiState(
                isSignupSuccessful = true
            )
        }
    }
}