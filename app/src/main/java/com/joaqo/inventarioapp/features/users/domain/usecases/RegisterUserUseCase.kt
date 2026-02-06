package com.joaqo.inventarioapp.features.users.domain.usecases

import android.util.Log
import com.joaqo.inventarioapp.features.users.domain.entities.User
import com.joaqo.inventarioapp.features.users.domain.repositories.UsersRepository

/**
 * Caso de uso: Registrar un nuevo usuario
 * @return Par de Usuario y Token JWT
 */
class RegisterUserUseCase(
    private val repository: UsersRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String
    ): Result<Pair<User, String>> {
        Log.d("RegisterUserUseCase", "=== INICIANDO VALIDACIONES ===")
        return try {
            // Validaciones de negocio
            if (fullName.isBlank()) {
                Log.e("RegisterUserUseCase", "❌ Nombre completo vacío")
                return Result.failure(Exception("El nombre completo no puede estar vacío"))
            }

            if (email.isBlank() || !isValidEmail(email)) {
                Log.e("RegisterUserUseCase", "❌ Email inválido: '$email'")
                return Result.failure(Exception("El email no es válido"))
            }

            if (password.length < 6) {
                Log.e("RegisterUserUseCase", "❌ Contraseña muy corta: ${password.length} chars")
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            Log.d("RegisterUserUseCase", "✅ Validaciones pasadas, llamando al repositorio...")
            val userWithToken = repository.registerUser(fullName, email, password)
            Log.d("RegisterUserUseCase", "✅ Repositorio respondió exitosamente")
            Result.success(userWithToken)
        } catch (e: Exception) {
            Log.e("RegisterUserUseCase", "❌ Error en UseCase: ${e.message}")
            Log.e("RegisterUserUseCase", "Stacktrace: ", e)
            Result.failure(e)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        Log.d("RegisterUserUseCase", "Email '$email' es válido: $isValid")
        return isValid
    }
}

