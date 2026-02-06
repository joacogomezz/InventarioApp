package com.joaqo.inventarioapp.features.products.domain.usecases

import com.joaqo.inventarioapp.features.products.domain.entities.Product
import com.joaqo.inventarioapp.features.products.domain.repositories.ProductsRepository

/**
 * Caso de uso: Crear un nuevo producto
 */
class CreateProductUseCase(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(
        name: String,
        price: Double,
        quantity: Int
    ): Result<Product> {
        return try {
            // Validaciones de negocio
            if (name.isBlank()) {
                return Result.failure(Exception("El nombre del producto no puede estar vacío"))
            }

            if (price < 0) {
                return Result.failure(Exception("El precio no puede ser negativo"))
            }

            if (quantity < 0) {
                return Result.failure(Exception("La cantidad no puede ser negativa"))
            }

            val product = repository.createProduct(name, price, quantity)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

