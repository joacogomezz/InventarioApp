package com.joaqo.inventarioapp.features.products.data.repositories

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joaqo.inventarioapp.core.network.InventaryApi
import com.joaqo.inventarioapp.features.products.data.datasources.remote.mapper.toDomain
import com.joaqo.inventarioapp.features.products.data.datasources.remote.model.ProductCreateRequest
import com.joaqo.inventarioapp.features.products.data.datasources.remote.model.ProductDataWrapper
import com.joaqo.inventarioapp.features.products.domain.entities.Product
import com.joaqo.inventarioapp.features.products.domain.repositories.ProductsRepository

/**
 * Implementación del repositorio de productos
 * Implementa la interface del dominio y se comunica con la API
 */
class ProductsRepositoryImpl(
    private val api: InventaryApi
) : ProductsRepository {

    override suspend fun getProducts(): List<Product> {
        val response = api.getProducts()

        // Manejar respuesta: puede ser una lista o 0 si no hay productos
        return when (val data = response.data) {
            is List<*> -> {
                // Convertir la lista de productos
                val gson = Gson()
                val json = gson.toJson(data)
                val type = object : TypeToken<List<ProductDataWrapper>>() {}.type
                val products: List<ProductDataWrapper> = gson.fromJson(json, type)
                products.map { it.toDomain() }
            }
            is Double -> {
                // data = 0, no hay productos
                emptyList()
            }
            else -> {
                emptyList()
            }
        }
    }

    override suspend fun getProductById(id: Int): Product {
        val response = api.getProductById(id)

        return response.data?.toDomain()
            ?: throw Exception("Producto con ID $id no encontrado")
    }

    override suspend fun createProduct(
        name: String,
        price: Double,
        quantity: Int
    ): Product {
        val request = ProductCreateRequest(
            name = name,
            price = price,
            quantity = quantity
        )

        val response = api.createProduct(request)

        return response.data?.toDomain()
            ?: throw Exception("Error al crear el producto")
    }

    override suspend fun updateProduct(
        id: Int,
        name: String,
        price: Double,
        quantity: Int
    ): Product {
        val request = ProductCreateRequest(
            name = name,
            price = price,
            quantity = quantity
        )

        val response = api.updateProduct(id, request)

        return response.data?.toDomain()
            ?: throw Exception("Error al actualizar el producto")
    }

    override suspend fun deleteProduct(id: Int): Boolean {
        return try {
            api.deleteProduct(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}

