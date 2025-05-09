package com.example.lottery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lottery.viewmodel.RifaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarRifaScreen(
    rifaId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: RifaViewModel = viewModel(factory = RifaViewModel.RifaViewModelFactory(LocalContext.current))
    val rifas by viewModel.rifas.collectAsState()
    val context = LocalContext.current
    val rifa = rifas.find { it.id == rifaId }

    // Si la rifa con el ID proporcionado existe, se muestra el formulario de edición.
    rifa?.let {
        var nombre by remember { mutableStateOf(it.nombre) }
        var descripcion by remember { mutableStateOf(it.descripcion) }
        var cantidadBoletos by remember { mutableStateOf(it.cantidadBoletos.toString()) }
        var valorUnitario by remember { mutableStateOf(it.valorUnitario.toString()) }
        var fechaSorteo by remember { mutableStateOf(Calendar.getInstance().apply { timeInMillis = it.fechaSorteo }) }
        var mostrarDatePicker by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                // Barra superior centrada con el título "Editar Rifa" y un botón para volver atrás.
                CenterAlignedTopAppBar(
                    title = { Text("Editar Rifa") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            },
            content = { paddingValues ->
                // Columna que contiene todos los elementos del formulario, con desplazamiento vertical si es necesario.
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo de texto para editar el nombre de la rifa.
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de la Rifa") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Campo de texto para editar la descripción de la rifa.
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Campo de texto para editar la cantidad de boletos, solo permite números.
                    OutlinedTextField(
                        value = cantidadBoletos,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) cantidadBoletos = it },
                        label = { Text("Cantidad de Boletos") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    // Campo de texto para editar el valor unitario del boleto, permite números y un punto decimal.
                    OutlinedTextField(
                        value = valorUnitario,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*(\\.\\d*)?$")))
                                valorUnitario = it
                        },
                        label = { Text("Valor por Boleto") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    // Fila para mostrar la fecha del sorteo y un botón para editarla.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Muestra la fecha del sorteo formateada.
                        Text(
                            text = "Fecha del Sorteo: ${SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(Date(fechaSorteo.timeInMillis))}",
                            modifier = Modifier.weight(1f)
                        )
                        // Botón para mostrar el diálogo del DatePicker.
                        Button(onClick = { mostrarDatePicker = true }) {
                            Text("Editar Fecha")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila con botones para cancelar o guardar los cambios.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Botón para volver a la pantalla anterior sin guardar cambios.
                        Button(onClick = onNavigateBack) {
                            Text("Cancelar")
                        }
                        // Botón para guardar los cambios realizados en la rifa.
                        Button(
                            onClick = {
                                // Verifica que todos los campos obligatorios estén llenos antes de guardar.
                                if (nombre.isNotBlank() && descripcion.isNotBlank() && cantidadBoletos.isNotBlank() && valorUnitario.isNotBlank()) {
                                    val cantidad = cantidadBoletos.toInt()
                                    val valor = valorUnitario.toDouble()
                                    val fecha = fechaSorteo.timeInMillis

                                    // Llama a la función del ViewModel para actualizar la rifa.
                                    viewModel.actualizarRifa(
                                        id = rifaId,
                                        nombre = nombre,
                                        descripcion = descripcion,
                                        cantidadBoletos = cantidad,
                                        valorUnitario = valor,
                                        fechaSorteo = fecha
                                    )
                                    onNavigateBack()
                                }
                            },
                            // El botón de guardar está habilitado solo si todos los campos obligatorios tienen contenido.
                            enabled = nombre.isNotBlank() && descripcion.isNotBlank() && cantidadBoletos.isNotBlank() && valorUnitario.isNotBlank()
                        ) {
                            Text("Guardar Cambios")
                        }
                    }
                }

                // Diálogo para seleccionar la fecha del sorteo.
                if (mostrarDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { mostrarDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                mostrarDatePicker = false
                            }) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDatePicker = false }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fechaSorteo.timeInMillis)
                        DatePicker(state = datePickerState)
                        LaunchedEffect(datePickerState.selectedDateMillis) {
                            datePickerState.selectedDateMillis?.let {
                                fechaSorteo = Calendar.getInstance().apply { timeInMillis = it }
                            }
                        }
                    }
                }
            }
        )
    } ?: run {
        // Si no se encuentra la rifa con el ID proporcionado, se muestra una pantalla de error.
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Error") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Muestra un mensaje de error en el centro de la pantalla.
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: Rifa no encontrada para editar.")
            }
        }
    }
}