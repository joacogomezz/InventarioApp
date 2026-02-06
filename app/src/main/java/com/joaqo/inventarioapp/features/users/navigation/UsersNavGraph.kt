package com.joaqo.inventarioapp.features.users.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.joaqo.inventarioapp.core.navigation.FeatureNavGraph
import com.joaqo.inventarioapp.core.navigation.Screens
import com.joaqo.inventarioapp.features.users.di.UsersModule
import com.joaqo.inventarioapp.features.users.presentation.screens.LoginScreen
import com.joaqo.inventarioapp.features.users.presentation.screens.RegisterScreen
import com.joaqo.inventarioapp.features.users.presentation.screens.UsersScreen
import com.joaqo.inventarioapp.features.users.presentation.viewmodels.LoginViewModel
import com.joaqo.inventarioapp.features.users.presentation.viewmodels.RegisterViewModel
import com.joaqo.inventarioapp.features.users.presentation.viewmodels.UsersViewModel

/**
 * Grafo de navegación para la feature de Users
 */
class UsersNavGraph(
    private val usersModule: UsersModule
) : FeatureNavGraph {

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        // Pantalla de Login
        navGraphBuilder.composable(Screens.LOGIN) {
            val viewModel: LoginViewModel = viewModel(
                factory = usersModule.provideLoginViewModelFactory()
            )

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    // Al hacer login exitoso, navegar a Products
                    navController.navigate(Screens.PRODUCTS) {
                        // Limpiar el backstack para que no pueda volver al login con back
                        popUpTo(Screens.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    // Navegar a la pantalla de registro
                    navController.navigate(Screens.REGISTER)
                }
            )
        }

        // Pantalla de Registro
        navGraphBuilder.composable(Screens.REGISTER) {
            val viewModel: RegisterViewModel = viewModel(
                factory = usersModule.provideRegisterViewModelFactory()
            )

            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    // Al registrarse exitosamente, simplemente volver atrás al Login
                    android.util.Log.d("UsersNavGraph", "✅ Registro exitoso - Volviendo al Login con popBackStack")
                    navController.popBackStack()
                },
                onBackToLogin = {
                    // Volver al login
                    android.util.Log.d("UsersNavGraph", "⬅️ Volviendo al login (botón atrás)")
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de lista de usuarios
        navGraphBuilder.composable(Screens.USERS) {

            val viewModel: UsersViewModel = viewModel(
                factory = usersModule.provideUsersViewModelFactory()
            )

            UsersScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigate(Screens.PRODUCTS) },
                onAddUserClick = {
                    // TODO: Navegar a pantalla de registrar usuario
                },
                onUserClick = { userId ->
                    // TODO: Navegar a detalle de usuario
                }
            )
        }
    }
}

