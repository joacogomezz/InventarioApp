package com.joaqo.inventarioapp.core.di
import android.content.Context
import com.joaqo.inventarioapp.BuildConfig
import com.joaqo.inventarioapp.core.network.InventaryApi
import com.joaqo.inventarioapp.features.products.data.repositories.ProductsRepositoryImpl
import com.joaqo.inventarioapp.features.products.domain.repositories.ProductsRepository
import com.joaqo.inventarioapp.features.users.data.repositories.UsersRepositoryImpl
import com.joaqo.inventarioapp.features.users.domain.repositories.UsersRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Contenedor de dependencias de la aplicación
 * Provee instancias compartidas de repositorios y servicios
 */
class AppContainer(context: Context) {

    init {
        android.util.Log.d("AppContainer", "=== INICIALIZANDO APP CONTAINER ===")
        android.util.Log.d("AppContainer", "BASE_URL: ${BuildConfig.BASE_URL}")
    }

    // Interceptor personalizado para manejo de errores y logging de respuestas
    private val errorInterceptor = Interceptor { chain ->
        try {
            val request = chain.request()
            android.util.Log.d("NetworkRequest", "→ ${request.method} ${request.url}")

            // Log del body del request si es POST o PUT
            if (request.method in listOf("POST", "PUT", "PATCH")) {
                val requestBody = request.body
                if (requestBody != null) {
                    val buffer = okio.Buffer()
                    requestBody.writeTo(buffer)
                    val requestBodyString = buffer.readUtf8()
                    android.util.Log.d("NetworkRequest", "Body: $requestBodyString")
                }
            }

            val response = chain.proceed(request)

            // Log de la respuesta
            android.util.Log.d("NetworkResponse", "← ${response.code} ${request.url}")

            // Log de los headers
            response.headers.names().forEach { headerName ->
                android.util.Log.d("NetworkResponse", "Header: $headerName = ${response.headers[headerName]}")
            }

            // Log del body de la respuesta (RAW) para debugging
            if (BuildConfig.DEBUG) {
                val responseBody = response.body
                val source = responseBody?.source()
                source?.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source?.buffer

                val responseBodyString = buffer?.clone()?.readUtf8() ?: ""
                if (responseBodyString.isNotEmpty()) {
                    android.util.Log.d("NetworkResponse", "Body RAW: $responseBodyString")
                }
            }

            response
        } catch (e: Exception) {
            android.util.Log.e("NetworkError", "❌ Error en red: ${e.message}", e)
            throw e
        }
    }

    // Cliente HTTP con configuración mejorada
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(errorInterceptor)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .retryOnConnectionFailure(true)
        .build()

    // Función para crear instancias de Retrofit
    private fun createRetrofit(baseUrl: String): Retrofit {
        // Gson configurado para ser permisivo con nulls y campos faltantes
        val gson = com.google.gson.GsonBuilder()
            .setLenient() // Permite JSON no estricto
            .serializeNulls() // Incluye campos null
            .create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Retrofit para la API de Inventario
    private val inventoryRetrofit = createRetrofit(BuildConfig.BASE_URL)

    // API de Inventario (lazy initialization)
    val inventaryApi: InventaryApi by lazy {
        inventoryRetrofit.create(InventaryApi::class.java)
    }

    // Repositorio de Productos (lazy initialization)
    val productsRepository: ProductsRepository by lazy {
        ProductsRepositoryImpl(inventaryApi)
    }

    // Repositorio de Usuarios (lazy initialization)
    val usersRepository: UsersRepository by lazy {
        UsersRepositoryImpl(inventaryApi)
    }
}