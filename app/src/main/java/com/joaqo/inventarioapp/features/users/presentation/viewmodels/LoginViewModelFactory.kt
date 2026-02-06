package com.joaqo.inventarioapp.features.users.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joaqo.inventarioapp.features.users.domain.usecases.LoginUserUseCase

/**
 * Factory para crear LoginViewModel con dependencias
 */
class LoginViewModelFactory(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginUserUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
