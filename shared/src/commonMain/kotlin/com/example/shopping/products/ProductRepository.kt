package com.example.shopping.products

class ProductRepository(
    private val api: ProductApi
) {

    suspend fun getProducts(): List<Product> {
        return api
            .getProducts()
            .products
    }
}