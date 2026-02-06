package com.joaqo.inventarioapp.features.users.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaqo.inventarioapp.features.users.domain.usecases.DeleteUserUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.GetUsersUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.LoginUserUseCase
import com.joaqo.inventarioapp.features.users.presentation.screens.LoginUiState
import com.joaqo.inventarioapp.features.users.presentation.screens.UsersUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsersViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = getUsersUseCase()

            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { usersList ->
                        currentState.copy(
                            isLoading = false,
                            users = usersList,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                    }
                )
            }
        }
    }

    fun refreshUsers() {
        _uiState.update { it.copy(isRefreshing = true, error = null) }

        viewModelScope.launch {
            val result = getUsersUseCase()

            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { usersList ->
                        currentState.copy(
                            isRefreshing = false,
                            users = usersList,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isRefreshing = false,
                            error = error.message ?: "Error al refrescar"
                        )
                    }
                )
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            val result = deleteUserUseCase(userId)

            result.fold(
                onSuccess = {
                    loadUsers()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Error al eliminar usuario")
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

