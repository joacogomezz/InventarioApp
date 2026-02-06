package com.joaqo.inventarioapp.features.users.domain.entities

/**
 * Entidad de dominio para Usuario
 * Modelo de negocio puro sin dependencias de frameworks externos
 */
data class User(
    val id: Int,
    val fullName: String,
    val email: String
)

