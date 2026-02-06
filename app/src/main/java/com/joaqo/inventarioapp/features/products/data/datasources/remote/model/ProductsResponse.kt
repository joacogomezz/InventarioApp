package com.joaqo.inventarioapp.features.products.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

/**
 * DTO: Response de la API cuando se obtiene una lista de productos
 * Basado en el formato JSON API de tu backend
 */
data class ProductsApiResponse(
    @SerializedName("data")
    val data: Any? // Puede ser List<ProductDto> o Int (0) cuando está vacío
)

/**
 * DTO: Response de la API cuando se obtiene un solo producto
 */
data class ProductApiResponse(
    @SerializedName("data")
    val data: ProductDataWrapper?
)

/**
 * Wrapper del producto en la respuesta JSON API
 */
data class ProductDataWrapper(
    @SerializedName("type")
    val type: String,

    @SerializedName("id")
    val id: Int,

    @SerializedName("attributes")
    val attributes: ProductDto
)

/**
 * DTO: Modelo de producto que viene del API
 */
data class ProductDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("quantity")
    val quantity: Int
)

/**
 * Request para crear o actualizar un producto
 */
data class ProductCreateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("quantity")
    val quantity: Int
)

