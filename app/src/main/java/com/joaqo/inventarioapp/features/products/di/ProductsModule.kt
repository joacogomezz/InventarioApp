package com.joaqo.inventarioapp.features.products.di

import com.joaqo.inventarioapp.core.di.AppContainer
import com.joaqo.inventarioapp.features.products.domain.usecases.CreateProductUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.DeleteProductUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.GetProductByIdUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.GetProductsUseCase
import com.joaqo.inventarioapp.features.products.domain.usecases.UpdateProductUseCase
import com.joaqo.inventarioapp.features.products.presentation.viewmodels.ProductsViewModelFactory

/**
 * Módulo de inyección de dependencias para la feature Products
 * Provee todos los casos de uso y factories necesarios
 */
class ProductsModule(
    private val appContainer: AppContainer
) {

    // ============ USE CASES ============

    private fun provideGetProductsUseCase(): GetProductsUseCase {
        return GetProductsUseCase(appContainer.productsRepository)
    }

    private fun provideGetProductByIdUseCase(): GetProductByIdUseCase {
        return GetProductByIdUseCase(appContainer.productsRepository)
    }

    private fun provideCreateProductUseCase(): CreateProductUseCase {
        return CreateProductUseCase(appContainer.productsRepository)
    }

    private fun provideUpdateProductUseCase(): UpdateProductUseCase {
        return UpdateProductUseCase(appContainer.productsRepository)
    }

    private fun provideDeleteProductUseCase(): DeleteProductUseCase {
        return DeleteProductUseCase(appContainer.productsRepository)
    }

    // ============ VIEW MODEL FACTORIES ============

    fun provideProductsViewModelFactory(): ProductsViewModelFactory {
        return ProductsViewModelFactory(
            getProductsUseCase = provideGetProductsUseCase(),
            createProductUseCase = provideCreateProductUseCase(),
            updateProductUseCase = provideUpdateProductUseCase(),
            deleteProductUseCase = provideDeleteProductUseCase()
        )
    }
}

