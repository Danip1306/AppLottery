package com.example.lottery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lottery.viewmodel.RifaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AñadirRifaScreen(onNavigateBack: () -> Unit) {
    val viewModel: RifaViewModel = viewModel(factory = RifaViewModel.RifaViewModelFactory(LocalContext.current))

    var nombreRifa by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioBoleto by remember { mutableStateOf("") }
    var totalBoletos by remember { mutableStateOf("") }

    var mostrarDialogoFecha by remember { mutableStateOf(false) }
    var fechaSorteoMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    val datePickerState = rememberDatePickerState()
    var fechaSorteo by remember { mutableStateOf("") }

    var precioError by remember { mutableStateOf(false) }
    var boletosError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Rifa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombreRifa,
                onValueChange = { nombreRifa = it },
                label = { Text("Nombre de la Rifa") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = precioBoleto,
                onValueChange = {
                    precioBoleto = it
                    precioError = it.toDoubleOrNull() == null && it.isNotBlank()
                },
                label = { Text("Precio por boleto") },
                modifier = Modifier.fillMaxWidth(),
                isError = precioError,
                supportingText = {
                    if (precioError) {
                        Text(text = "Ingrese un valor numérico")
                    }
                }
            )
            OutlinedTextField(
                value = totalBoletos,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toIntOrNull() ?: 0 <= 100) {
                        totalBoletos = newValue
                        boletosError = newValue.toIntOrNull() == null && newValue.isNotBlank()
                    }
                },
                label = { Text("Total de boletos (Máximo 100)") },
                modifier = Modifier.fillMaxWidth(),
                isError = boletosError,
                supportingText = {
                    if (boletosError) {
                        Text(text = "Ingrese un valor numérico entero")
                    } else if (totalBoletos.toIntOrNull() ?: 0 > 100) {
                        Text(text = "El número máximo de boletos es 100")
                    }
                }
            )

            Button(
                onClick = { mostrarDialogoFecha = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(text = "Fecha de sorteo: ${sdf.format(Date(fechaSorteoMillis))}")
            }

            Button(
                onClick = {
                    val precio = precioBoleto.toDoubleOrNull()
                    val boletos = totalBoletos.toIntOrNull()

                    if (nombreRifa.isNotBlank() && precio != null && boletos != null && boletos > 0) {
                        viewModel.insertarRifa(
                            nombre = nombreRifa,
                            descripcion = descripcion,
                            cantidadBoletos = boletos,
                            valorUnitario = precio,
                            fechaSorteo = fechaSorteoMillis
                        )
                        onNavigateBack()
                    } else {
                        if (precio == null && precioBoleto.isNotBlank()) {
                            precioError = true
                        }
                        if (boletos == null && totalBoletos.isNotBlank()) {
                            boletosError = true
                        }
                        println("Por favor, completa todos los campos correctamente con valores numéricos.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !precioError && !boletosError // Deshabilitar el botón si hay errores numéricos
            ) {
                Text("Guardar Rifa")
            }
        }

        if (mostrarDialogoFecha) {
            DatePickerDialog(
                onDismissRequest = { mostrarDialogoFecha = false },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogoFecha = false
                        if (datePickerState.selectedDateMillis != null) {
                            fechaSorteoMillis = datePickerState.selectedDateMillis!!
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            fechaSorteo = sdf.format(Date(fechaSorteoMillis))
                        }
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoFecha = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}