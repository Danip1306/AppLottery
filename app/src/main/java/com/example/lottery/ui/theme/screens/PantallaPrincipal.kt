package com.example.lottery.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lottery.R
import com.example.lottery.data.model.Rifa
import com.example.lottery.viewmodel.RifaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(onNavigateToDetalle: (Int) -> Unit, onNavigateToAddRifa: () -> Unit) {
    val context = LocalContext.current
    val viewModel: RifaViewModel = viewModel(factory = RifaViewModel.RifaViewModelFactory(context))
    val rifas by viewModel.rifas.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Rifas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddRifa) {
                Icon(Icons.Filled.Add, "Añadir Rifa")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::actualizarTextoBusqueda,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                label = { Text("Buscar rifa...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (rifas.isEmpty()) {
                Text("No hay rifas creadas.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(rifas) { rifa ->
                        RifaItem(rifa = rifa, onClick = { onNavigateToDetalle(rifa.id) })
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun RifaItem(rifa: Rifa, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(rifa.nombre, style = MaterialTheme.typography.titleMedium)
            Text("${rifa.cantidadBoletos} boletos - $${rifa.valorUnitario} c/u", style = MaterialTheme.typography.bodySmall)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaSorteo = Date(rifa.fechaSorteo)
            Text("Sorteo: ${sdf.format(fechaSorteo)}", style = MaterialTheme.typography.bodySmall)
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "Ver detalles",
            modifier = Modifier.size(24.dp)
        )
    }
}