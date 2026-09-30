package com.example.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.shopping.core.session.SessionManager
import com.example.shopping.core.session.SessionStorage
import com.example.shopping.products.ProductViewModel
import com.example.shopping.products.ProductsScreen
import com.example.shopping.signup.SignupScreen
import com.example.shopping.signup.SignupViewModel

@Composable
fun App(
    sessionStorage: SessionStorage
) {
    val sessionManager = remember {
        SessionManager(sessionStorage)
    }

    var isLoggedIn by remember {
        mutableStateOf(
            sessionManager.isSessionValid()
        )
    }

    if (isLoggedIn) {

        val productsViewModel = remember {
            ProductViewModel()
        }

        ProductsScreen(
            viewModel = productsViewModel,
            onLogout = {
                sessionManager.clearSession()
                isLoggedIn = false
            }
        )

    } else {

        val signupViewModel = remember {
            SignupViewModel()
        }

        SignupScreen(
            viewModel = signupViewModel,
            onExploreProducts = {
                sessionManager.createSession()
                isLoggedIn = true
            }
        )
    }
}