package com.joaqo.inventarioapp.features.users.domain.repositories

import com.joaqo.inventarioapp.features.users.domain.entities.User

/**
 * Interface del repositorio de usuarios
 * Define QUÉ hacer, no CÓMO hacerlo
 */
interface UsersRepository {

    /**
     * Obtiene todos los usuarios
     */
    suspend fun getUsers(): List<User>

    /**
     * Obtiene un usuario por su ID
     */
    suspend fun getUserById(id: Int): User

    /**
     * Registra un nuevo usuario
     */
    suspend fun registerUser(fullName: String, email: String, password: String): Pair<User, String>

    /**
     * Login de usuario
     * @return Par de Usuario y Token JWT
     */
    suspend fun loginUser(email: String, password: String): Pair<User, String>

    /**
     * Actualiza un usuario existente
     */
    suspend fun updateUser(id: Int, fullName: String, email: String, password: String): User

    /**
     * Elimina un usuario (soft delete)
     */
    suspend fun deleteUser(id: Int): Boolean
}

