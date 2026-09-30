package com.example.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.shopping.core.session.AndroidSessionStorage

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                sessionStorage = AndroidSessionStorage(this)
            )
        }
    }
}