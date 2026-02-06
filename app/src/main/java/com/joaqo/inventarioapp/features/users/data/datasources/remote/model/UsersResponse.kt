package com.joaqo.inventarioapp.features.users.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Response de la API cuando se obtiene una lista de usuarios
 */
data class UsersApiResponse(
    @SerializedName("data")
    val data: Any? = null // Puede ser List<UserDataWrapper> o Int (0) cuando está vacío
)

/**
 * Response de la API cuando se obtiene un solo usuario
 */
data class UserApiResponse(
    @SerializedName("data")
    val data: UserDataWrapper? = null
)

/**
 * Wrapper del usuario en la respuesta JSON API
 * Coincide con el formato de tu API Go:
 * {
 *   "data": {
 *     "type": "users",
 *     "id": 1,
 *     "attributes": {
 *       "full_name": "joaquin",
 *       "email": "joaquin12@gmail.com"
 *     }
 *   }
 * }
 */
data class UserDataWrapper(
    @SerializedName("type")
    val type: String = "users",

    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("attributes")
    val attributes: UserDto = UserDto()
)

/**
 * DTO: Modelo de usuario que viene del API
 */
data class UserDto(
    @SerializedName("full_name")
    val fullName: String = "",

    @SerializedName("email")
    val email: String = ""
)

