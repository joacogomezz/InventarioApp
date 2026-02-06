package com.joaqo.inventarioapp.features.users.domain.usecases

import com.joaqo.inventarioapp.features.users.domain.repositories.UsersRepository

/**
 * Caso de uso: Eliminar un usuario
 */
class DeleteUserUseCase(
    private val repository: UsersRepository
) {
    suspend operator fun invoke(userId: Int): Result<Boolean> {
        return try {
            if (userId <= 0) {
                return Result.failure(Exception("ID de usuario inválido"))
            }

            val success = repository.deleteUser(userId)

            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("No se pudo eliminar el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

