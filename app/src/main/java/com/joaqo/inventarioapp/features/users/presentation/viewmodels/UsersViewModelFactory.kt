package com.joaqo.inventarioapp.features.users.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joaqo.inventarioapp.features.users.domain.usecases.DeleteUserUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.GetUsersUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.LoginUserUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.RegisterUserUseCase

class UsersViewModelFactory(
    private val getUsersUseCase: GetUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(
                getUsersUseCase = getUsersUseCase,
                deleteUserUseCase = deleteUserUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

