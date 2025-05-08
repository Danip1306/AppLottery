package com.example.lottery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun DetalleRifaScreen(rifaId: Int, onNavigateBack: () -> Unit) {
    val viewModel: RifaViewModel = viewModel(factory = RifaViewModel.RifaViewModelFactory(LocalContext.current))
    val rifa by viewModel.rifas.collectAsState()

    val rifaDetalle = rifa.find { it.id == rifaId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de la Rifa") },
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
            rifaDetalle?.let {
                Text("Nombre: ${it.nombre}", style = MaterialTheme.typography.headlineSmall)
                Text("Descripción: ${it.descripcion}", style = MaterialTheme.typography.bodyLarge) // Mostramos la descripción
                Text("Precio por boleto: $${it.valorUnitario}", style = MaterialTheme.typography.bodyMedium)
                Text("Total de boletos: ${it.cantidadBoletos}", style = MaterialTheme.typography.bodyMedium)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaSorteo = Date(it.fechaSorteo)
                Text("Fecha de sorteo: ${sdf.format(fechaSorteo)}", style = MaterialTheme.typography.bodyMedium)
                // Aquí podrías añadir más detalles o acciones para la rifa
            } ?: run {
                Text("Rifa no encontrada.")
            }
        }
    }
}