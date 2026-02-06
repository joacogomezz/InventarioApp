package com.joaqo.inventarioapp.features.products.domain.entities

/**
 * Entidad de dominio para Producto
 * Representa el modelo de negocio puro sin dependencias de frameworks externos
 */
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)

