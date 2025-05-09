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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AñadirRifaScreen(onNavigateBack: () -> Unit) {
    // Obtiene el ViewModel para la lógica de la rifa.
    val viewModel: RifaViewModel = viewModel(factory = RifaViewModel.RifaViewModelFactory(LocalContext.current))

    // Estados para los campos de entrada de la rifa.
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cantidadBoletos by remember { mutableStateOf("") }
    var valorUnitario by remember { mutableStateOf("") }
    var fechaSorteo by remember { mutableStateOf(Calendar.getInstance()) }

    // Estado para controlar la visibilidad del DatePicker.
    var mostrarDatePicker by remember { mutableStateOf(false) }

    // Estructura principal de la pantalla con barra superior y contenido.
    Scaffold(
        topBar = {
            // Barra superior con título y botón de navegación.
            CenterAlignedTopAppBar(
                title = { Text("Añadir Nueva Rifa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        content = { paddingValues ->
            // Contenido principal de la pantalla en una columna desplazable.
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campos de texto para los detalles de la rifa.
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre de la Rifa") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cantidadBoletos, onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) cantidadBoletos = it }, label = { Text("Cantidad de Boletos") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = valorUnitario, onValueChange = { valorUnitario = it }, label = { Text("Valor por Boleto") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                // Fila para seleccionar la fecha del sorteo.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fecha del Sorteo: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaSorteo.timeInMillis))}",
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { mostrarDatePicker = true }) {
                        Text("Seleccionar Fecha")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para guardar la rifa.
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && descripcion.isNotBlank() && cantidadBoletos.isNotBlank() && valorUnitario.isNotBlank()) {
                            val cantidad = cantidadBoletos.toInt()
                            val valor = valorUnitario.toDouble()
                            val fecha = fechaSorteo.timeInMillis
                            viewModel.insertarRifa(nombre, descripcion, cantidad, valor, fecha)
                            onNavigateBack()
                        }
                    },
                    enabled = nombre.isNotBlank() && descripcion.isNotBlank() && cantidadBoletos.isNotBlank() && valorUnitario.isNotBlank()
                ) {
                    Text("Guardar Rifa")
                }
            }

            // Diálogo para seleccionar la fecha.
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
}