package com.joaqo.inventarioapp.features.products.domain.usecases

import com.joaqo.inventarioapp.features.products.domain.repositories.ProductsRepository

/**
 * Caso de uso: Eliminar un producto
 */
class DeleteProductUseCase(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: Int): Result<Boolean> {
        return try {
            if (productId <= 0) {
                return Result.failure(Exception("ID de producto inválido"))
            }

            val success = repository.deleteProduct(productId)

            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("No se pudo eliminar el producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

