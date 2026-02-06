package com.joaqo.inventarioapp.features.users.domain.usecases

import android.util.Log
import com.joaqo.inventarioapp.features.users.domain.entities.User
import com.joaqo.inventarioapp.features.users.domain.repositories.UsersRepository

/**
 * Caso de uso: Login de usuario
 * @return Par de Usuario y Token JWT
 */
class LoginUserUseCase(
    private val repository: UsersRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Pair<User, String>> {
        Log.d("LoginUserUseCase", "=== INICIANDO VALIDACIONES DE LOGIN ===")
        return try {
            // Validaciones de negocio
            if (email.isBlank()) {
                Log.e("LoginUserUseCase", "❌ Email vacío")
                return Result.failure(Exception("El email no puede estar vacío"))
            }

            if (!isValidEmail(email)) {
                Log.e("LoginUserUseCase", "❌ Email inválido: '$email'")
                return Result.failure(Exception("El email no es válido"))
            }

            if (password.isBlank()) {
                Log.e("LoginUserUseCase", "❌ Contraseña vacía")
                return Result.failure(Exception("La contraseña no puede estar vacía"))
            }

            Log.d("LoginUserUseCase", "✅ Validaciones pasadas, llamando al repositorio...")
            val userWithToken = repository.loginUser(email, password)
            Log.d("LoginUserUseCase", "✅ Repositorio respondió exitosamente")
            Result.success(userWithToken)
        } catch (e: Exception) {
            Log.e("LoginUserUseCase", "❌ Error en UseCase: ${e.message}")
            Log.e("LoginUserUseCase", "Stacktrace: ", e)
            Result.failure(e)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        Log.d("LoginUserUseCase", "Email '$email' es válido: $isValid")
        return isValid
    }
}

