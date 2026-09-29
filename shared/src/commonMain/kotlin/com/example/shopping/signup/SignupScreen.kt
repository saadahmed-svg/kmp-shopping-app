package com.example.shopping.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SignupScreen(
    viewModel: SignupViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AnimatedContent(
            targetState = uiState.isSignupSuccessful,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "signupContent"
        ) { isSuccessful ->

            if (isSuccessful) {
                SignupSuccessScreen(
                    onBackToSignup = {
                        viewModel.onIntent(
                            SignupIntent.BackToSignup
                        )
                    }
                )
            } else {
                SignupForm(
                    uiState = uiState,
                    onIntent = viewModel::onIntent
                )
            }
        }

        if (uiState.isSignupSuccessful) {
            ConfettiAnimation()
        }
    }
}

@Composable
private fun SignupForm(
    uiState: SignupUiState,
    onIntent: (SignupIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 24.dp,
                vertical = 32.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        KmpLogo()

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = "Create your account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Enter your details to get started",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = {
                onIntent(
                    SignupIntent.NameChanged(it)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Name")
            },
            placeholder = {
                Text("Optional")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        EmailField(
            uiState = uiState,
            onIntent = onIntent
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        PasswordField(
            uiState = uiState,
            onIntent = onIntent
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Button(
            onClick = {
                onIntent(
                    SignupIntent.Submit
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = uiState.canSubmit,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Create account")
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
private fun KmpLogo() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(
                color = Color(0xFF7F52FF),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "KMP",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun EmailField(
    uiState: SignupUiState,
    onIntent: (SignupIntent) -> Unit
) {
    val hasError =
        uiState.isEmailTouched &&
                uiState.emailError != null

    val borderColor by animateColorAsState(
        targetValue = if (hasError) {
            Color(0xFFE53935)
        } else {
            MaterialTheme.colorScheme.outline
        },
        label = "emailBorder"
    )

    OutlinedTextField(
        value = uiState.email,
        onValueChange = {
            onIntent(
                SignupIntent.EmailChanged(it)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Email")
        },
        placeholder = {
            Text("you@example.com")
        },
        singleLine = true,
        isError = hasError,
        supportingText = {
            AnimatedVisibility(
                visible = hasError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = uiState.emailError.orEmpty()
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            errorBorderColor = Color(0xFFE53935)
        )
    )
}

@Composable
private fun PasswordField(
    uiState: SignupUiState,
    onIntent: (SignupIntent) -> Unit
) {
    val strengthColor by animateColorAsState(
        targetValue = when (uiState.passwordStrength) {
            PasswordStrength.NONE ->
                MaterialTheme.colorScheme.outline

            PasswordStrength.WEAK ->
                Color(0xFFE53935)

            PasswordStrength.MEDIUM ->
                Color(0xFFF9A825)

            PasswordStrength.STRONG ->
                Color(0xFF43A047)
        },
        label = "passwordBorder"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = uiState.password,
            onValueChange = {
                onIntent(
                    SignupIntent.PasswordChanged(it)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Password")
            },
            singleLine = true,
            visualTransformation =
                if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            trailingIcon = {
                TextButton(
                    onClick = {
                        onIntent(
                            SignupIntent.TogglePasswordVisibility
                        )
                    }
                ) {
                    Text(
                        if (uiState.isPasswordVisible) {
                            "Hide"
                        } else {
                            "Show"
                        }
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = strengthColor,
                unfocusedBorderColor = strengthColor
            )
        )

        AnimatedVisibility(
            visible = uiState.password.isNotEmpty(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {

                PasswordStrengthIndicator(
                    strength = uiState.passwordStrength
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = when (uiState.passwordStrength) {
                        PasswordStrength.NONE -> ""
                        PasswordStrength.WEAK -> "Weak password"
                        PasswordStrength.MEDIUM -> "Medium password"
                        PasswordStrength.STRONG -> "Strong password"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = strengthColor
                )
            }
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(
    strength: PasswordStrength
) {
    val activeSegments = when (strength) {
        PasswordStrength.NONE -> 0
        PasswordStrength.WEAK -> 1
        PasswordStrength.MEDIUM -> 2
        PasswordStrength.STRONG -> 3
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        repeat(3) { index ->

            val color by animateColorAsState(
                targetValue = if (index < activeSegments) {
                    when (strength) {
                        PasswordStrength.WEAK ->
                            Color(0xFFE53935)

                        PasswordStrength.MEDIUM ->
                            Color(0xFFF9A825)

                        PasswordStrength.STRONG ->
                            Color(0xFF43A047)

                        PasswordStrength.NONE ->
                            MaterialTheme.colorScheme.surfaceVariant
                    }
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                label = "strengthSegment$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
private fun SignupSuccessScreen(
    onBackToSignup: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {

        Column(
            modifier = Modifier.align(
                Alignment.Center
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎉",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Signed up successfully",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        TextButton(
            onClick = onBackToSignup,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Text("Back to signup")
        }
    }
}
