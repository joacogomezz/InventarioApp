package com.joaqo.inventarioapp.features.products.data.datasources.remote.mapper

import com.joaqo.inventarioapp.features.products.data.datasources.remote.model.ProductDataWrapper
import com.joaqo.inventarioapp.features.products.data.datasources.remote.model.ProductDto
import com.joaqo.inventarioapp.features.products.domain.entities.Product

/**
 * Mapper: Convierte ProductDataWrapper (DTO) a Product (Entity)
 * Extension function para transformación limpia
 */
fun ProductDataWrapper.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.attributes.name,
        price = this.attributes.price,
        quantity = this.attributes.quantity
    )
}

/**
 * Mapper: Convierte ProductDto con ID a Product (Entity)
 * Para cuando el DTO viene sin el wrapper
 */
fun ProductDto.toDomain(id: Int): Product {
    return Product(
        id = id,
        name = this.name,
        price = this.price,
        quantity = this.quantity
    )
}

