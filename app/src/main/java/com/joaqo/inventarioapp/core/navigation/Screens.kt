package com.joaqo.inventarioapp.core.navigation

// Rutas de navegación como constantes String
object Screens {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PRODUCTS = "products"
    const val PRODUCT_CREATE = "products/create"
    const val PRODUCT_EDIT = "products/edit/{productId}"
    const val PRODUCT_DETAIL = "products/{productId}"
    const val USERS = "users"
    const val USER_DETAIL = "users/{userId}"

    fun productEdit(productId: Int) = "products/edit/$productId"
    fun productDetail(productId: Int) = "products/$productId"
    fun userDetail(userId: Int) = "users/$userId"
}

