package com.joaqo.inventarioapp.features.users.domain.usecases

import com.joaqo.inventarioapp.features.users.domain.entities.User
import com.joaqo.inventarioapp.features.users.domain.repositories.UsersRepository

/**
 * Caso de uso: Obtener todos los usuarios
 */
class GetUsersUseCase(
    private val repository: UsersRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return try {
            val users = repository.getUsers()

            // Lógica de negocio: Filtrar usuarios con email vacío
            val validUsers = users.filter { it.email.isNotBlank() }

            if (validUsers.isEmpty()) {
                Result.failure(Exception("No se encontraron usuarios"))
            } else {
                Result.success(validUsers)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

