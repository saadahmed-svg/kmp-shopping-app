package com.example.shopping

import androidx.compose.ui.window.ComposeUIViewController
import com.example.shopping.core.session.IosSessionStorage

fun MainViewController() =
    ComposeUIViewController {

        App(
            sessionStorage = IosSessionStorage()
        )
    }