package com.joaqo.inventarioapp.features.users.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Request para registrar un usuario
 */
data class UserRegisterRequest(
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password_hash")
    val passwordHash: String
)

/**
 * Request para hacer login
 */
data class UserLoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password_hash")
    val passwordHash: String
)

