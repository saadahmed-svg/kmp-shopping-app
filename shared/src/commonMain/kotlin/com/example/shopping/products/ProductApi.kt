package com.example.shopping.products

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProductApi(
    private val client: HttpClient
) {

    suspend fun getProducts(): ProductResponse {
        return client
            .get("https://dummyjson.com/products?limit=30")
            .body()
    }
}