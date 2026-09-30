//Instead of letting the UI directly modify the ViewModel, we'll send events/intents.

package com.example.shopping.signup

sealed interface SignupIntent {

    data class NameChanged(
        val value: String
    ) : SignupIntent

    data class EmailChanged(
        val value: String
    ) : SignupIntent

    data class PasswordChanged(
        val value: String
    ) : SignupIntent

    data object EmailFocusLost : SignupIntent

    data object PasswordFocusLost : SignupIntent

    data object TogglePasswordVisibility : SignupIntent

    data object Submit : SignupIntent

}