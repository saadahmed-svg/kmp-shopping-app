package com.example.shopping.products

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortOrder: ProductSortOrder = ProductSortOrder.NONE
)