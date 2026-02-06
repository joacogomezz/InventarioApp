package com.joaqo.inventarioapp.features.products.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.joaqo.inventarioapp.core.navigation.FeatureNavGraph
import com.joaqo.inventarioapp.core.navigation.Screens
import com.joaqo.inventarioapp.features.products.di.ProductsModule
import com.joaqo.inventarioapp.features.products.presentation.screens.ProductFormScreen
import com.joaqo.inventarioapp.features.products.presentation.screens.ProductsScreen
import com.joaqo.inventarioapp.features.products.presentation.viewmodels.ProductsViewModel

/**
 * Grafo de navegación para la feature de Products
 * Registra todas las rutas relacionadas con productos
 */
class ProductsNavGraph(
    private val productsModule: ProductsModule
) : FeatureNavGraph {

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        // Pantalla principal de productos
        navGraphBuilder.composable(Screens.PRODUCTS) {
            android.util.Log.d("ProductsNavGraph", "Navegando a lista de productos")

            val viewModel: ProductsViewModel = viewModel(
                factory = productsModule.provideProductsViewModelFactory()
            )

            ProductsScreen(
                viewModel = viewModel,
                onBackClick = {
                    android.util.Log.d("ProductsNavGraph", "Navegando a Users desde Products")
                    navController.navigate(Screens.USERS)
                },
                onAddProductClick = {
                    android.util.Log.d("ProductsNavGraph", "Navegando a crear producto")
                    navController.navigate(Screens.PRODUCT_CREATE)
                },
                onEditProductClick = { productId ->
                    android.util.Log.d("ProductsNavGraph", "Navegando a editar producto: $productId")
                    navController.navigate(Screens.productEdit(productId))
                }
            )
        }

        // Pantalla de crear producto
        navGraphBuilder.composable(Screens.PRODUCT_CREATE) {
            android.util.Log.d("ProductsNavGraph", "Pantalla: Crear Producto")

            val viewModel: ProductsViewModel = viewModel(
                factory = productsModule.provideProductsViewModelFactory()
            )

            ProductFormScreen(
                viewModel = viewModel,
                productId = null,
                onNavigateBack = {
                    android.util.Log.d("ProductsNavGraph", "Volviendo a lista de productos desde crear")
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de editar producto
        navGraphBuilder.composable(
            route = Screens.PRODUCT_EDIT,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            android.util.Log.d("ProductsNavGraph", "Pantalla: Editar Producto - ID: $productId")

            val viewModel: ProductsViewModel = viewModel(
                factory = productsModule.provideProductsViewModelFactory()
            )

            ProductFormScreen(
                viewModel = viewModel,
                productId = productId,
                onNavigateBack = {
                    android.util.Log.d("ProductsNavGraph", "Volviendo a lista de productos desde editar")
                    navController.popBackStack()
                }
            )
        }
    }
}

