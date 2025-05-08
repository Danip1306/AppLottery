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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AñadirRifaScreen(onNavigateBack: () -> Unit) {
    val viewModel: RifaViewModel = viewModel(factory = RifaViewModel.RifaViewModelFactory(LocalContext.current))

    var nombreRifa by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioBoleto by remember { mutableStateOf("") }
    var totalBoletos by remember { mutableStateOf("") }
    var fechaSorteo by remember { mutableStateOf("") }

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
                onValueChange = { precioBoleto = it },
                label = { Text("Precio por boleto") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = totalBoletos,
                onValueChange = { totalBoletos = it },
                label = { Text("Total de boletos") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fechaSorteo,
                onValueChange = { fechaSorteo = it },
                label = { Text("Fecha de sorteo (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    // Aquí convertiremos la fecha y guardaremos la rifa
                    val parsedDate = try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.parse(fechaSorteo)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis() // Usar fecha actual en caso de error de formato
                    }

                    val precio = precioBoleto.toDoubleOrNull() ?: 0.0
                    val boletos = totalBoletos.toIntOrNull() ?: 0

                    if (nombreRifa.isNotBlank() && precio > 0 && boletos > 0 && fechaSorteo.isNotBlank()) {
                        viewModel.insertarRifa(
                            nombre = nombreRifa,
                            cantidadBoletos = boletos,
                            valorUnitario = precio,
                            fechaSorteo = parsedDate
                        )
                        onNavigateBack() // Volver a la pantalla principal después de guardar
                    } else {
                        // Puedes mostrar un mensaje de error si algún campo es inválido
                        println("Por favor, completa todos los campos correctamente.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Rifa")
            }
        }
    }
}