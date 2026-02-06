package com.joaqo.inventarioapp.features.products.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joaqo.inventarioapp.features.products.presentation.viewmodels.ProductsViewModel

/**
 * Pantalla de formulario para crear o editar un producto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: ProductsViewModel,
    productId: Int? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val isEditMode = productId != null

    // Cargar datos del producto si es edición
    LaunchedEffect(productId) {
        if (productId != null) {
            android.util.Log.d("ProductFormScreen", "Modo edición - ProductId: $productId")
            val product = uiState.products.find { it.id == productId }
            product?.let {
                name = it.name
                price = it.price.toString()
                quantity = it.quantity.toString()
                android.util.Log.d("ProductFormScreen", "Producto cargado: ${it.name}")
            }
        } else {
            android.util.Log.d("ProductFormScreen", "Modo creación")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) "Editar Producto" else "Nuevo Producto")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo: Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nombre del producto *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    enabled = !isLoading
                )

                // Campo: Precio
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            price = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Precio *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    enabled = !isLoading,
                    prefix = { Text("$") }
                )

                // Campo: Cantidad
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                            quantity = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Cantidad *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    enabled = !isLoading
                )

                // Mensaje de error
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de guardar
                Button(
                    onClick = {
                        // Validaciones
                        when {
                            name.isBlank() -> {
                                errorMessage = "El nombre es requerido"
                                return@Button
                            }
                            price.isBlank() -> {
                                errorMessage = "El precio es requerido"
                                return@Button
                            }
                            price.toDoubleOrNull() == null || price.toDouble() <= 0 -> {
                                errorMessage = "El precio debe ser un número válido mayor a 0"
                                return@Button
                            }
                            quantity.isBlank() -> {
                                errorMessage = "La cantidad es requerida"
                                return@Button
                            }
                            quantity.toIntOrNull() == null || quantity.toInt() < 0 -> {
                                errorMessage = "La cantidad debe ser un número válido mayor o igual a 0"
                                return@Button
                            }
                        }

                        // Limpiar foco
                        focusManager.clearFocus()
                        isLoading = true

                        // Crear o actualizar producto
                        if (isEditMode && productId != null) {
                            android.util.Log.d("ProductFormScreen", "Actualizando producto: $productId")
                            viewModel.updateProduct(
                                productId = productId,
                                name = name.trim(),
                                price = price.toDouble(),
                                quantity = quantity.toInt()
                            )
                        } else {
                            android.util.Log.d("ProductFormScreen", "Creando nuevo producto")
                            viewModel.createProduct(
                                name = name.trim(),
                                price = price.toDouble(),
                                quantity = quantity.toInt()
                            )
                        }

                        // Simular delay para la operación
                        isLoading = false
                        showSuccessDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isEditMode) "Actualizar Producto" else "Crear Producto")
                    }
                }
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = {
                Text(
                    if (isEditMode)
                        "El producto ha sido actualizado correctamente"
                    else
                        "El producto ha sido creado correctamente"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

