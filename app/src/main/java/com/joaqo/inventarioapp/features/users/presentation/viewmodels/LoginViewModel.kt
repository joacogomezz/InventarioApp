package com.joaqo.inventarioapp.features.users.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaqo.inventarioapp.features.users.domain.usecases.LoginUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de UI para la pantalla de Login
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de Login
 */
class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Limpiar mensajes de error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Resetear el estado de login exitoso
     */
    fun resetLoginState() {
        _uiState.update { it.copy(isLoggedIn = false, error = null) }
    }

    /**
     * Iniciar sesión
     */
    fun login(email: String, password: String) {
        Log.d("LoginViewModel", "=== INICIANDO LOGIN ===")
        Log.d("LoginViewModel", "Email: '$email'")
        Log.d("LoginViewModel", "Password length: ${password.length}")

        // Validaciones previas
        if (email.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    error = "El email es requerido"
                )
            }
            return
        }

        if (password.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    error = "La contraseña es requerida"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Actualizando estado a loading...")
                _uiState.update { it.copy(isLoading = true, error = null) }

                Log.d("LoginViewModel", "Llamando al UseCase...")
                val result = loginUserUseCase(email, password)

                Log.d("LoginViewModel", "UseCase completado, procesando resultado...")
                result.fold(
                    onSuccess = { (user, token) ->
                        Log.d("LoginViewModel", "✅ LOGIN EXITOSO")
                        Log.d("LoginViewModel", "Usuario: ${user.fullName} (${user.email})")
                        Log.d("LoginViewModel", "Token recibido: ${token.take(20)}...")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                error = null
                            )
                        }
                        Log.d("LoginViewModel", "Estado actualizado a SUCCESS")
                    },
                    onFailure = { exception ->
                        Log.e("LoginViewModel", "❌ ERROR EN LOGIN")
                        Log.e("LoginViewModel", "Error: ${exception.message}")
                        Log.e("LoginViewModel", "Stacktrace: ", exception)

                        val errorMessage = when {
                            exception.message?.contains("Email o contraseña incorrectos", ignoreCase = true) == true ->
                                "Email o contraseña incorrectos"
                            exception.message?.contains("Usuario no encontrado", ignoreCase = true) == true ->
                                "Usuario no encontrado"
                            exception.message?.contains("network", ignoreCase = true) == true ->
                                "Error de conexión. Verifique su internet"
                            exception.message?.contains("timeout", ignoreCase = true) == true ->
                                "Tiempo de espera agotado. Intente nuevamente"
                            else -> exception.message ?: "Error desconocido al iniciar sesión"
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoggedIn = false,
                                error = errorMessage
                            )
                        }
                        Log.d("LoginViewModel", "Estado actualizado a ERROR: $errorMessage")
                    }
                )
            } catch (e: Exception) {
                Log.e("LoginViewModel", "❌ EXCEPCIÓN NO MANEJADA")
                Log.e("LoginViewModel", "Error: ${e.message}")
                Log.e("LoginViewModel", "Stacktrace: ", e)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    )
                }
            }
        }
    }
}
