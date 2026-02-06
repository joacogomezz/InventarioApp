package com.joaqo.inventarioapp.features.products.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaqo.inventarioapp.features.products.domain.usecases.CreateProductUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.DeleteProductUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.GetProductsUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.UpdateProductUseCase
import com.joaqo.inventarioapp.features.products.presentation.screens.ProductsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de lista de productos
 * Maneja el estado de UI y la lógica de presentación
 */
class ProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    // Estado privado mutable
    private val _uiState = MutableStateFlow(ProductsUiState())

    // Estado público inmutable para la UI
    val uiState = _uiState.asStateFlow()

    init {
        try {
            loadProducts()
        } catch (e: Exception) {
            android.util.Log.e("ProductsViewModel", "Error en init", e)
            _uiState.update { it.copy(isLoading = false, error = "Error al inicializar") }
        }
    }

    /**
     * Carga la lista de productos
     */
    fun loadProducts() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = getProductsUseCase()

                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { productsList ->
                            currentState.copy(
                                isLoading = false,
                                products = productsList,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            currentState.copy(
                                isLoading = false,
                                error = error.message ?: "Error desconocido"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ProductsViewModel", "Error en loadProducts", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar productos: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Refresca la lista de productos (pull-to-refresh)
     */
    fun refreshProducts() {
        _uiState.update { it.copy(isRefreshing = true, error = null) }

        viewModelScope.launch {
            val result = getProductsUseCase()

            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { productsList ->
                        currentState.copy(
                            isRefreshing = false,
                            products = productsList,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isRefreshing = false,
                            error = error.message ?: "Error al refrescar"
                        )
                    }
                )
            }
        }
    }

    /**
     * Elimina un producto
     */
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            val result = deleteProductUseCase(productId)

            result.fold(
                onSuccess = {
                    // Recargar la lista después de eliminar
                    loadProducts()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Error al eliminar producto")
                    }
                }
            )
        }
    }

    /**
     * Crea un nuevo producto
     */
    fun createProduct(name: String, price: Double, quantity: Int) {
        android.util.Log.d("ProductsViewModel", "Creando producto: $name")

        viewModelScope.launch {
            try {
                val result = createProductUseCase(name, price, quantity)

                result.fold(
                    onSuccess = { createdProduct ->
                        android.util.Log.d("ProductsViewModel", "✅ Producto creado: ${createdProduct.name}")
                        // Recargar la lista después de crear
                        loadProducts()
                    },
                    onFailure = { error ->
                        android.util.Log.e("ProductsViewModel", "❌ Error al crear producto: ${error.message}")
                        _uiState.update {
                            it.copy(error = error.message ?: "Error al crear producto")
                        }
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("ProductsViewModel", "❌ Excepción al crear producto", e)
                _uiState.update {
                    it.copy(error = "Error inesperado: ${e.message}")
                }
            }
        }
    }

    /**
     * Actualiza un producto existente
     */
    fun updateProduct(productId: Int, name: String, price: Double, quantity: Int) {
        android.util.Log.d("ProductsViewModel", "Actualizando producto ID: $productId - $name")

        viewModelScope.launch {
            try {
                val result = updateProductUseCase(productId, name, price, quantity)

                result.fold(
                    onSuccess = { updatedProduct ->
                        android.util.Log.d("ProductsViewModel", "✅ Producto actualizado: ${updatedProduct.name}")
                        // Recargar la lista después de actualizar
                        loadProducts()
                    },
                    onFailure = { error ->
                        android.util.Log.e("ProductsViewModel", "❌ Error al actualizar producto: ${error.message}")
                        _uiState.update {
                            it.copy(error = error.message ?: "Error al actualizar producto")
                        }
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("ProductsViewModel", "❌ Excepción al actualizar producto", e)
                _uiState.update {
                    it.copy(error = "Error inesperado: ${e.message}")
                }
            }
        }
    }

    /**
     * Limpia el error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

