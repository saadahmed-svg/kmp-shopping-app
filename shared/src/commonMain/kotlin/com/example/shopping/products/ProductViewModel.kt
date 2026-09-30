package com.example.shopping.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping.core.network.createHttpClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val client: HttpClient =
        createHttpClient()

    private val repository =
        ProductRepository(
            ProductApi(client)
        )

    private val _uiState =
        MutableStateFlow(ProductUiState())

    val uiState: StateFlow<ProductUiState> =
        _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun sortProducts(
        sortOrder: ProductSortOrder
    ) {
        _uiState.update { state ->

            val sortedProducts =
                when (sortOrder) {

                    ProductSortOrder.NONE ->
                        state.products

                    ProductSortOrder.PRICE_LOW_TO_HIGH ->
                        state.products.sortedBy {
                            it.price
                        }

                    ProductSortOrder.PRICE_HIGH_TO_LOW ->
                        state.products.sortedByDescending {
                            it.price
                        }
                }

            state.copy(
                products = sortedProducts,
                sortOrder = sortOrder
            )
        }
    }

    fun retry() {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {

                val products =
                    repository.getProducts()

                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    )
                }

            } catch (exception: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error =
                            exception.message
                                ?: "Something went wrong"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        client.close()
        super.onCleared()
    }
}