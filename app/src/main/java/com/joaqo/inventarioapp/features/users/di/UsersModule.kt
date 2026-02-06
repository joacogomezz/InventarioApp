package com.joaqo.inventarioapp.features.users.di

import com.joaqo.inventarioapp.core.di.AppContainer
import com.joaqo.inventarioapp.features.users.domain.usecases.DeleteUserUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.GetUsersUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.LoginUserUseCase
import com.joaqo.inventarioapp.features.users.domain.usecases.RegisterUserUseCase
import com.joaqo.inventarioapp.features.users.presentation.viewmodels.LoginViewModelFactory
import com.joaqo.inventarioapp.features.users.presentation.viewmodels.RegisterViewModelFactory
import com.joaqo.inventarioapp.features.users.presentation.viewmodels.UsersViewModelFactory

/**
 * Módulo de inyección de dependencias para la feature Users
 */
class UsersModule(
    private val appContainer: AppContainer
) {

    // ============ USE CASES ============

    private fun provideGetUsersUseCase(): GetUsersUseCase {
        return GetUsersUseCase(appContainer.usersRepository)
    }

    private fun provideRegisterUserUseCase(): RegisterUserUseCase {
        return RegisterUserUseCase(appContainer.usersRepository)
    }

    private fun provideLoginUserUseCase(): LoginUserUseCase {
        return LoginUserUseCase(appContainer.usersRepository)
    }

    private fun provideDeleteUserUseCase(): DeleteUserUseCase {
        return DeleteUserUseCase(appContainer.usersRepository)
    }

    // ============ VIEW MODEL FACTORIES ============

    fun provideUsersViewModelFactory(): UsersViewModelFactory {
        return UsersViewModelFactory(
            getUsersUseCase = provideGetUsersUseCase(),
            deleteUserUseCase = provideDeleteUserUseCase()
        )
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        return LoginViewModelFactory(
            loginUserUseCase = provideLoginUserUseCase()
        )
    }

    fun provideRegisterViewModelFactory(): RegisterViewModelFactory {
        return RegisterViewModelFactory(
            registerUserUseCase = provideRegisterUserUseCase()
        )
    }
}

