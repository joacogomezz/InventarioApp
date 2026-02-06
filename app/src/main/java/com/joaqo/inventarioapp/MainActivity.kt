package com.joaqo.inventarioapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.joaqo.inventarioapp.core.di.AppContainer
import com.joaqo.inventarioapp.core.navigation.Screens
import com.joaqo.inventarioapp.core.ui.theme.INVENTARIOAPPTheme
import com.joaqo.inventarioapp.features.products.di.ProductsModule
import com.joaqo.inventarioapp.features.products.navigation.ProductsNavGraph
import com.joaqo.inventarioapp.features.users.di.UsersModule
import com.joaqo.inventarioapp.features.users.navigation.UsersNavGraph

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        android.util.Log.d("MainActivity", "=== INICIANDO APLICACIÓN ===")

        try {
            // Inicializar AppContainer con Context
            android.util.Log.d("MainActivity", "Inicializando AppContainer...")
            appContainer = AppContainer(applicationContext)
            android.util.Log.d("MainActivity", "✅ AppContainer inicializado correctamente")

            enableEdgeToEdge()
            setContent {
                INVENTARIOAPPTheme {
                    InventarioApp(appContainer)
                }
            }
            android.util.Log.d("MainActivity", "✅ UI configurada correctamente")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "❌ Error crítico en onCreate", e)
            android.util.Log.e("MainActivity", "Error: ${e.message}", e)
            throw e
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("MainActivity", "MainActivity destruida")
    }
}

@Composable
fun InventarioApp(appContainer: AppContainer) {
    android.util.Log.d("InventarioApp", "=== INICIALIZANDO NAVEGACIÓN ===")

    val navController = rememberNavController()

    // Inicializar módulos de features
    android.util.Log.d("InventarioApp", "Inicializando módulos...")
    val productsModule = remember { ProductsModule(appContainer) }
    val usersModule = remember { UsersModule(appContainer) }
    android.util.Log.d("InventarioApp", " Módulos inicializados")

    // Registrar grafos de navegación
    val productsNavGraph = remember { ProductsNavGraph(productsModule) }
    val usersNavGraph = remember { UsersNavGraph(usersModule) }
    android.util.Log.d("InventarioApp", " Grafos de navegación creados")
    android.util.Log.d("InventarioApp", "Iniciando NavHost con startDestination: ${Screens.LOGIN}")

    NavHost(
        navController = navController,
        startDestination = Screens.LOGIN
    ) {
        // Registrar grafo de productos PRIMERO
        productsNavGraph.registerGraph(this, navController)

        // Registrar grafo de usuarios (incluye Login y Register)
        usersNavGraph.registerGraph(this, navController)
    }

    android.util.Log.d("InventarioApp", "NavHost configurado correctamente")
}

