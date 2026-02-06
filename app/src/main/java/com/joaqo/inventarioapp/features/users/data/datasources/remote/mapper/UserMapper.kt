package com.joaqo.inventarioapp.features.users.data.datasources.remote.mapper

import android.util.Log
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UserDataWrapper
import com.joaqo.inventarioapp.features.users.domain.entities.User

fun UserDataWrapper.toDomain(): User {
    Log.d("UserMapper", "=== MAPEANDO USUARIO ===")
    Log.d("UserMapper", "ID: ${this.id}")
    Log.d("UserMapper", "Type: ${this.type}")
    Log.d("UserMapper", "Attributes: ${this.attributes}")
    Log.d("UserMapper", "Attributes fullName: '${this.attributes.fullName}'")
    Log.d("UserMapper", "Attributes email: '${this.attributes.email}'")

    return try {
        // Validar que el ID sea válido
        if (this.id <= 0) {
            Log.w("UserMapper", " ID de usuario inválido: ${this.id}, usando valor por defecto")
        }

        // Validar y limpiar fullName
        val cleanFullName = this.attributes.fullName.trim()
        if (cleanFullName.isBlank()) {
            Log.w("UserMapper", "⚠️ Nombre de usuario vacío")
        }

        // Validar y limpiar email
        val cleanEmail = this.attributes.email.trim()
        if (cleanEmail.isBlank()) {
            Log.w("UserMapper", " Email de usuario vacío")
        }

        User(
            id = if (this.id > 0) this.id else 1,
            fullName = if (cleanFullName.isNotBlank()) cleanFullName else "Usuario",
            email = if (cleanEmail.isNotBlank()) cleanEmail else "no-email@example.com"
        ).also {
            Log.d("UserMapper", " Usuario mapeado exitosamente: ${it.fullName} (${it.email})")
        }
    } catch (e: Exception) {
        Log.e("UserMapper", " Error al mapear usuario", e)
        Log.e("UserMapper", "Detalles del error: ${e.message}")
        Log.e("UserMapper", "UserDataWrapper completo: $this")
        Log.e("UserMapper", "Attributes completo: ${this.attributes}")

        // En lugar de lanzar excepción, retornar un usuario por defecto con los datos que tengamos
        User(
            id = if (this.id > 0) this.id else 1,
            fullName = this.attributes.fullName.trim().ifBlank { "Usuario" },
            email = this.attributes.email.trim().ifBlank { "no-email@example.com" }
        )
    }
}

