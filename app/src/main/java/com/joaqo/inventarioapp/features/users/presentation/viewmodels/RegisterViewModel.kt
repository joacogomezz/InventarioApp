package com.joaqo.inventarioapp.features.users.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaqo.inventarioapp.features.users.domain.usecases.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de UI para la pantalla de Registro
 */
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de Registro
 */
class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Limpiar mensajes de error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Resetear el estado de registro exitoso
     */
    fun resetRegistrationState() {
        _uiState.update { it.copy(isRegistered = false, error = null) }
    }

    /**
     * Registrar un nuevo usuario
     */
    fun register(fullName: String, email: String, password: String) {
        Log.d("RegisterViewModel", "=== INICIANDO REGISTRO ===")
        Log.d("RegisterViewModel", "Nombre: '$fullName'")
        Log.d("RegisterViewModel", "Email: '$email'")
        Log.d("RegisterViewModel", "Password length: ${password.length}")

        // Validaciones previas para evitar llamadas innecesarias
        if (fullName.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRegistered = false,
                    error = "El nombre completo es requerido"
                )
            }
            return
        }

        if (email.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRegistered = false,
                    error = "El email es requerido"
                )
            }
            return
        }

        if (password.length < 6) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRegistered = false,
                    error = "La contraseña debe tener al menos 6 caracteres"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                Log.d("RegisterViewModel", "Actualizando estado a loading...")
                _uiState.update { it.copy(isLoading = true, error = null) }

                Log.d("RegisterViewModel", "Llamando al UseCase...")
                val result = registerUserUseCase(fullName, email, password)

                Log.d("RegisterViewModel", "UseCase completado, procesando resultado...")
                result.fold(
                    onSuccess = { (user, token) ->
                        Log.d("RegisterViewModel", "✅ REGISTRO EXITOSO")
                        Log.d("RegisterViewModel", "Usuario: ${user.fullName} (${user.email})")
                        Log.d("RegisterViewModel", "Token recibido: ${token.take(20)}...")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRegistered = true,
                                error = null
                            )
                        }
                        Log.d("RegisterViewModel", "Estado actualizado a SUCCESS")
                    },
                    onFailure = { exception ->
                        Log.e("RegisterViewModel", "❌ ERROR EN REGISTRO")
                        Log.e("RegisterViewModel", "Error: ${exception.message}")
                        Log.e("RegisterViewModel", "Stacktrace: ", exception)

                        val errorMessage = when {
                            exception.message?.contains("already exists", ignoreCase = true) == true ->
                                "Este email ya está registrado"
                            exception.message?.contains("invalid email", ignoreCase = true) == true ->
                                "El formato del email no es válido"
                            exception.message?.contains("network", ignoreCase = true) == true ->
                                "Error de conexión. Verifique su internet"
                            exception.message?.contains("timeout", ignoreCase = true) == true ->
                                "Tiempo de espera agotado. Intente nuevamente"
                            else -> exception.message ?: "Error desconocido al registrarse"
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRegistered = false,
                                error = errorMessage
                            )
                        }
                        Log.d("RegisterViewModel", "Estado actualizado a ERROR: $errorMessage")
                    }
                )
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "❌ EXCEPCIÓN NO MANEJADA")
                Log.e("RegisterViewModel", "Error: ${e.message}")
                Log.e("RegisterViewModel", "Stacktrace: ", e)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegistered = false,
                        error = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    )
                }
            }
        }
    }
}

