package com.joaqo.inventarioapp.features.products.presentation.screens

import com.joaqo.inventarioapp.features.products.domain.entities.Product

/**
 * Estado de UI para la pantalla de productos
 * Representa todos los estados posibles de la interfaz
 */
data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * Estado de UI para el detalle de un producto
 */
data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null
)

/**
 * Estado de UI para crear/editar producto
 */
data class ProductFormUiState(
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

