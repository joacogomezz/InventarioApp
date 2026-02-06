package com.joaqo.inventarioapp.core.network

import com.joaqo.inventarioapp.features.products.data.datasources.remote.model.ProductApiResponse
import com.joaqo.inventarioapp.features.products.data.datasources.remote.model.ProductCreateRequest
import com.joaqo.inventarioapp.features.products.data.datasources.remote.model.ProductsApiResponse
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UserApiResponse
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UserLoginRequest
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UserRegisterRequest
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UsersApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interface de Retrofit para la API de Inventario
 * Define todos los endpoints de productos y usuarios
 */
interface InventaryApi {

    // ============ PRODUCTOS ============

    @GET("v1/products")
    suspend fun getProducts(): ProductsApiResponse

    @GET("v1/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductApiResponse

    @POST("v1/products")
    suspend fun createProduct(@Body product: ProductCreateRequest): ProductApiResponse

    @PUT("v1/products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: ProductCreateRequest): ProductApiResponse

    @DELETE("v1/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): ProductApiResponse

    // ============ USUARIOS ============

    @GET("v1/users")
    suspend fun getUsers(): UsersApiResponse

    @GET("v1/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserApiResponse

    @POST("v1/users")
    suspend fun registerUser(@Body user: UserRegisterRequest): Response<UserApiResponse>

    @POST("v1/users/login")
    suspend fun loginUser(@Body credentials: UserLoginRequest): Response<UserApiResponse>

    @PUT("v1/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserRegisterRequest): UserApiResponse

    @DELETE("v1/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): UserApiResponse
}

