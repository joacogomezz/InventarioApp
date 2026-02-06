package com.joaqo.inventarioapp.features.products.domain.repositories

import com.joaqo.inventarioapp.features.products.domain.entities.Product

/**
 * Interface del repositorio de productos
 * Define QUÉ hacer, no CÓMO hacerlo
 * La implementación está en la capa de datos
 */
interface ProductsRepository {

    /**
     * Obtiene todos los productos
     */
    suspend fun getProducts(): List<Product>

    /**
     * Obtiene un producto por su ID
     */
    suspend fun getProductById(id: Int): Product

    /**
     * Crea un nuevo producto
     */
    suspend fun createProduct(name: String, price: Double, quantity: Int): Product

    /**
     * Actualiza un producto existente
     */
    suspend fun updateProduct(id: Int, name: String, price: Double, quantity: Int): Product

    /**
     * Elimina un producto
     */
    suspend fun deleteProduct(id: Int): Boolean
}

