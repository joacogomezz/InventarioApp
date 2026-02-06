package com.joaqo.inventarioapp.features.products.domain.usecases

import com.joaqo.inventarioapp.features.products.domain.entities.Product
import com.joaqo.inventarioapp.features.products.domain.repositories.ProductsRepository

/**
 * Caso de uso: Obtener todos los productos
 * Encapsula la lógica de negocio para obtener la lista de productos
 */
class GetProductsUseCase(
    private val repository: ProductsRepository
) {
    /**
     * Ejecuta el caso de uso
     * @return Result con la lista de productos o un error
     */
    suspend operator fun invoke(): Result<List<Product>> {
        return try {
            val products = repository.getProducts()

            // Lógica de negocio: Filtrar productos con nombre vacío
            val validProducts = products.filter { it.name.isNotBlank() }

            if (validProducts.isEmpty()) {
                Result.failure(Exception("No se encontraron productos válidos"))
            } else {
                Result.success(validProducts)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

