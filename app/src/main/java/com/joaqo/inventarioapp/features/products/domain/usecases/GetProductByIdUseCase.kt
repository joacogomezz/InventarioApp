package com.joaqo.inventarioapp.features.products.domain.usecases

import com.joaqo.inventarioapp.features.products.domain.entities.Product
import com.joaqo.inventarioapp.features.products.domain.repositories.ProductsRepository

/**
 * Caso de uso: Obtener un producto por ID
 */
class GetProductByIdUseCase(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: Int): Result<Product> {
        return try {
            if (productId <= 0) {
                return Result.failure(Exception("ID de producto inválido"))
            }

            val product = repository.getProductById(productId)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

