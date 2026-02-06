package com.joaqo.inventarioapp.features.products.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joaqo.inventarioapp.features.products.domain.usecases.CreateProductUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.DeleteProductUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.GetProductsUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.UpdateProductUseCase

/**
 * Factory para crear ProductsViewModel con dependencias
 * Necesario porque el ViewModel tiene parámetros en el constructor
 */
class ProductsViewModelFactory(
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            return ProductsViewModel(
                getProductsUseCase = getProductsUseCase,
                createProductUseCase = createProductUseCase,
                updateProductUseCase = updateProductUseCase,
                deleteProductUseCase = deleteProductUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

