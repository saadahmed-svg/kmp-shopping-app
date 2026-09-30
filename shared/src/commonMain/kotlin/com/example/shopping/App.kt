package com.example.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.shopping.products.ProductViewModel
import com.example.shopping.products.ProductsScreen
import com.example.shopping.signup.SignupScreen
import com.example.shopping.signup.SignupViewModel

@Composable
fun App() {

    var showProducts by remember {
        mutableStateOf(false)
    }

    if (showProducts) {

        val productsViewModel =
            remember {
                ProductViewModel()
            }

        ProductsScreen(
            viewModel = productsViewModel,
            onLogout = {
                showProducts = false
            }
        )

    } else {

        val signupViewModel =
            remember {
                SignupViewModel()
            }

        SignupScreen(
            viewModel = signupViewModel,
            onExploreProducts = {
                showProducts = true
            }
        )
    }
}

