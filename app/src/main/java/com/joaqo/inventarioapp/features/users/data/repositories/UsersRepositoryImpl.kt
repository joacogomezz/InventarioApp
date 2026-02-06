package com.joaqo.inventarioapp.features.users.data.repositories

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joaqo.inventarioapp.core.network.InventaryApi
import com.joaqo.inventarioapp.features.users.data.datasources.remote.mapper.toDomain
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UserDataWrapper
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UserLoginRequest
import com.joaqo.inventarioapp.features.users.data.datasources.remote.model.UserRegisterRequest
import com.joaqo.inventarioapp.features.users.domain.entities.User
import com.joaqo.inventarioapp.features.users.domain.repositories.UsersRepository

class UsersRepositoryImpl(
    private val api: InventaryApi
) : UsersRepository {

    override suspend fun getUsers(): List<User> {
        val response = api.getUsers()

        return when (val data = response.data) {
            is List<*> -> {
                val gson = Gson()
                val json = gson.toJson(data)
                val type = object : TypeToken<List<UserDataWrapper>>() {}.type
                val users: List<UserDataWrapper> = gson.fromJson(json, type)
                users.map { it.toDomain() }
            }
            is Double -> emptyList()
            else -> emptyList()
        }
    }

    override suspend fun getUserById(id: Int): User {
        val response = api.getUserById(id)
        return response.data?.toDomain()
            ?: throw Exception("Usuario con ID $id no encontrado")
    }

    override suspend fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): Pair<User, String> {
        Log.d("UsersRepositoryImpl", "=== LLAMADA AL API ===")
        Log.d("UsersRepositoryImpl", "URL: https://joaquinproducts.chuy7x.space/v1/users")
        Log.d("UsersRepositoryImpl", "Preparando request...")

        return try {
            val request = UserRegisterRequest(
                fullName = fullName.trim(),
                email = email.trim(),
                passwordHash = password
            )

            Log.d("UsersRepositoryImpl", "Request: fullName='$fullName', email='$email'")
            Log.d("UsersRepositoryImpl", "Enviando request al servidor...")

            val response = api.registerUser(request)

            Log.d("UsersRepositoryImpl", "Respuesta recibida:")
            Log.d("UsersRepositoryImpl", "- Status Code: ${response.code()}")
            Log.d("UsersRepositoryImpl", "- Is Successful: ${response.isSuccessful}")
            Log.d("UsersRepositoryImpl", "- Message: ${response.message()}")

            when {
                !response.isSuccessful -> {
                    val errorBody = try {
                        response.errorBody()?.string() ?: "Sin detalles del error"
                    } catch (e: Exception) {
                        "Error al leer respuesta del servidor"
                    }

                    Log.e("UsersRepositoryImpl", "❌ Request falló")
                    Log.e("UsersRepositoryImpl", "Error body: $errorBody")

                    val errorMessage = when (response.code()) {
                        400 -> "Datos inválidos. Verifique la información ingresada"
                        409 -> "Este email ya está registrado"
                        500 -> "Error en el servidor. Intente más tarde"
                        else -> "Error al registrar: ${response.code()}"
                    }

                    throw Exception(errorMessage)
                }

                response.body() == null -> {
                    Log.e("UsersRepositoryImpl", "❌ Respuesta vacía del servidor")
                    throw Exception("El servidor no envió una respuesta válida")
                }

                else -> {
                    val userResponse = response.body()!!

                    Log.d("UsersRepositoryImpl", "✅ Respuesta del servidor exitosa")
                    Log.d("UsersRepositoryImpl", "Response body: $userResponse")
                    Log.d("UsersRepositoryImpl", "User data: ${userResponse.data}")
                    Log.d("UsersRepositoryImpl", "User data type: ${userResponse.data?.type}")
                    Log.d("UsersRepositoryImpl", "User data id: ${userResponse.data?.id}")
                    Log.d("UsersRepositoryImpl", "User data attributes: ${userResponse.data?.attributes}")

                    if (userResponse.data == null) {
                        Log.e("UsersRepositoryImpl", "❌ Data es null en la respuesta")
                        throw Exception("El servidor no retornó datos de usuario")
                    }

                    Log.d("UsersRepositoryImpl", "Intentando mapear usuario a dominio...")
                    val user = try {
                        userResponse.data.toDomain()
                    } catch (e: Exception) {
                        Log.e("UsersRepositoryImpl", "❌ Error al mapear usuario", e)
                        throw Exception("Error al procesar los datos del usuario: ${e.message}")
                    }
                    Log.d("UsersRepositoryImpl", "✅ Usuario mapeado: ${user.fullName} (ID: ${user.id})")

                    // Extraer token del header Authorization
                    val authHeader = response.headers()["Authorization"]
                        ?: response.headers()["authorization"]
                        ?: ""

                    Log.d("UsersRepositoryImpl", "Headers recibidos:")
                    response.headers().names().forEach { headerName ->
                        Log.d("UsersRepositoryImpl", "- $headerName: ${response.headers()[headerName]}")
                    }

                    val token = when {
                        authHeader.startsWith("Bearer ", ignoreCase = true) ->
                            authHeader.substring(7)
                        authHeader.isNotEmpty() -> authHeader
                        else -> {
                            Log.w("UsersRepositoryImpl", "⚠️ No se encontró token en headers")
                            ""
                        }
                    }

                    Log.d("UsersRepositoryImpl", "Token extraído: '${token.take(20)}...'")
                    Log.d("UsersRepositoryImpl", "✅ REGISTRO COMPLETADO")

                    Pair(user, token)
                }
            }

        } catch (e: java.net.SocketTimeoutException) {
            Log.e("UsersRepositoryImpl", "❌ TIMEOUT", e)
            throw Exception("Tiempo de espera agotado. Verifique su conexión a internet")
        } catch (e: java.net.UnknownHostException) {
            Log.e("UsersRepositoryImpl", "❌ NO INTERNET", e)
            throw Exception("Sin conexión a internet. Verifique su red")
        } catch (e: java.net.ConnectException) {
            Log.e("UsersRepositoryImpl", "❌ CONNECTION ERROR", e)
            throw Exception("Error de conexión. Intente nuevamente")
        } catch (e: com.google.gson.JsonSyntaxException) {
            Log.e("UsersRepositoryImpl", "❌ JSON SYNTAX ERROR", e)
            Log.e("UsersRepositoryImpl", "Error detallado: ${e.message}")
            Log.e("UsersRepositoryImpl", "Causa: ${e.cause?.message}")
            throw Exception("Error al procesar la respuesta del servidor. Formato JSON inválido.")
        } catch (e: com.google.gson.JsonParseException) {
            Log.e("UsersRepositoryImpl", "❌ JSON PARSE ERROR", e)
            Log.e("UsersRepositoryImpl", "Error detallado: ${e.message}")
            throw Exception("Error al interpretar los datos del servidor")
        } catch (e: NullPointerException) {
            Log.e("UsersRepositoryImpl", "❌ NULL POINTER ERROR", e)
            Log.e("UsersRepositoryImpl", "Error detallado: ${e.message}")
            Log.e("UsersRepositoryImpl", "StackTrace:", e)
            throw Exception("Datos incompletos recibidos del servidor")
        } catch (e: IllegalArgumentException) {
            Log.e("UsersRepositoryImpl", "❌ ILLEGAL ARGUMENT ERROR", e)
            Log.e("UsersRepositoryImpl", "Error detallado: ${e.message}")
            throw Exception("Datos inválidos: ${e.message}")
        } catch (e: retrofit2.HttpException) {
            Log.e("UsersRepositoryImpl", "❌ HTTP ERROR", e)
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos"
                401 -> "No autorizado"
                409 -> "Email ya registrado"
                500 -> "Error del servidor"
                else -> "Error HTTP ${e.code()}"
            }
            throw Exception(errorMessage)
        } catch (e: Exception) {
            Log.e("UsersRepositoryImpl", "❌ EXCEPCIÓN GENERAL", e)
            Log.e("UsersRepositoryImpl", "Tipo: ${e.javaClass.simpleName}")
            Log.e("UsersRepositoryImpl", "Mensaje: ${e.message}")
            Log.e("UsersRepositoryImpl", "StackTrace:", e)

            // Si ya es un mensaje personalizado, no lo envolvemos
            val message = when {
                e.message?.contains("Error al registrar") == true ||
                e.message?.contains("Datos inválidos") == true ||
                e.message?.contains("email ya está registrado") == true ||
                e.message?.contains("Error en el servidor") == true ||
                e.message?.contains("conexión") == true ||
                e.message?.contains("Error al mapear") == true ||
                e.message?.contains("Error al procesar") == true -> e.message
                else -> "Error inesperado al registrar: ${e.message ?: "Desconocido"}"
            }

            throw Exception(message)
        }
    }

    override suspend fun loginUser(email: String, password: String): Pair<User, String> {
        Log.d("UsersRepositoryImpl", "=== LOGIN AL API ===")
        Log.d("UsersRepositoryImpl", "Email: '$email'")

        return try {
            val request = UserLoginRequest(
                email = email.trim(),
                passwordHash = password
            )

            Log.d("UsersRepositoryImpl", "Enviando login request...")
            val response = api.loginUser(request)

            Log.d("UsersRepositoryImpl", "Login respuesta:")
            Log.d("UsersRepositoryImpl", "- Status Code: ${response.code()}")
            Log.d("UsersRepositoryImpl", "- Is Successful: ${response.isSuccessful}")

            when {
                !response.isSuccessful -> {
                    val errorBody = try {
                        response.errorBody()?.string() ?: "Sin detalles del error"
                    } catch (e: Exception) {
                        "Error al leer respuesta del servidor"
                    }

                    Log.e("UsersRepositoryImpl", "❌ Login falló")
                    Log.e("UsersRepositoryImpl", "Error body: $errorBody")

                    val errorMessage = when (response.code()) {
                        401 -> "Email o contraseña incorrectos"
                        404 -> "Usuario no encontrado"
                        500 -> "Error en el servidor. Intente más tarde"
                        else -> "Error al iniciar sesión: ${response.code()}"
                    }

                    throw Exception(errorMessage)
                }

                response.body() == null -> {
                    Log.e("UsersRepositoryImpl", "❌ Respuesta vacía del servidor en login")
                    throw Exception("El servidor no envió una respuesta válida")
                }

                else -> {
                    val userResponse = response.body()!!

                    Log.d("UsersRepositoryImpl", "✅ Login exitoso")

                    val user = userResponse.data?.toDomain()
                        ?: throw Exception("Credenciales inválidas")

                    // Extraer token del header Authorization
                    val authHeader = response.headers()["Authorization"]
                        ?: response.headers()["authorization"]
                        ?: ""

                    val token = when {
                        authHeader.startsWith("Bearer ", ignoreCase = true) ->
                            authHeader.substring(7)
                        authHeader.isNotEmpty() -> authHeader
                        else -> {
                            Log.w("UsersRepositoryImpl", "⚠️ No se encontró token en headers de login")
                            ""
                        }
                    }

                    Log.d("UsersRepositoryImpl", "Token de login extraído: '${token.take(20)}...'")

                    Pair(user, token)
                }
            }

        } catch (e: java.net.SocketTimeoutException) {
            Log.e("UsersRepositoryImpl", "❌ LOGIN TIMEOUT", e)
            throw Exception("Tiempo de espera agotado. Verifique su conexión a internet")
        } catch (e: java.net.UnknownHostException) {
            Log.e("UsersRepositoryImpl", "❌ LOGIN NO INTERNET", e)
            throw Exception("Sin conexión a internet. Verifique su red")
        } catch (e: java.net.ConnectException) {
            Log.e("UsersRepositoryImpl", "❌ LOGIN CONNECTION ERROR", e)
            throw Exception("Error de conexión. Intente nuevamente")
        } catch (e: com.google.gson.JsonSyntaxException) {
            Log.e("UsersRepositoryImpl", "❌ LOGIN JSON ERROR", e)
            throw Exception("Error al procesar la respuesta del servidor")
        } catch (e: retrofit2.HttpException) {
            Log.e("UsersRepositoryImpl", "❌ LOGIN HTTP ERROR", e)
            val errorMessage = when (e.code()) {
                401 -> "Email o contraseña incorrectos"
                404 -> "Usuario no encontrado"
                500 -> "Error del servidor"
                else -> "Error HTTP ${e.code()}"
            }
            throw Exception(errorMessage)
        } catch (e: Exception) {
            Log.e("UsersRepositoryImpl", "❌ LOGIN EXCEPCIÓN GENERAL", e)
            Log.e("UsersRepositoryImpl", "Tipo: ${e.javaClass.simpleName}")
            Log.e("UsersRepositoryImpl", "Mensaje: ${e.message}")

            val message = when {
                e.message?.contains("Credenciales inválidas") == true ||
                e.message?.contains("Email o contraseña") == true ||
                e.message?.contains("Usuario no encontrado") == true ||
                e.message?.contains("conexión") == true -> e.message
                else -> "Error inesperado al iniciar sesión: ${e.message ?: "Desconocido"}"
            }

            throw Exception(message)
        }
    }

    override suspend fun updateUser(
        id: Int,
        fullName: String,
        email: String,
        password: String
    ): User {
        val request = UserRegisterRequest(
            fullName = fullName,
            email = email,
            passwordHash = password
        )

        val response = api.updateUser(id, request)
        return response.data?.toDomain()
            ?: throw Exception("Error al actualizar usuario")
    }

    override suspend fun deleteUser(id: Int): Boolean {
        return try {
            api.deleteUser(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}

