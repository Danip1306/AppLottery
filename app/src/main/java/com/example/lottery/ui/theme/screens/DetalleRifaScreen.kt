package com.example.lottery.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lottery.viewmodel.RifaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRifaScreen(rifaId: Int, onNavigateBack: () -> Unit, onNavigateToEditRifa: (Int) -> Unit) {

    val viewModel: RifaViewModel = viewModel(factory = RifaViewModel.RifaViewModelFactory(LocalContext.current))
    val rifas by viewModel.rifas.collectAsState()
    val numerosOcupadosMap by viewModel.numerosOcupados.collectAsState()
    val context = LocalContext.current

    val rifaDetalle by remember(rifaId, rifas) {
        derivedStateOf { rifas.find { it.id == rifaId } }
    }

    val numerosOcupadosGuardados by remember(rifaId) { derivedStateOf { numerosOcupadosMap[rifaId] ?: emptyList() } }
    val ocupadosLocal = remember { mutableStateListOf<Int>() }
    var expanded by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var numeroGanador by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(rifaId) {
        ocupadosLocal.clear()
        ocupadosLocal.addAll(numerosOcupadosGuardados)
    }

    // Estado para almacenar el número que el usuario ingresa manualmente.
    var numeroIngresado by remember { mutableStateOf("") }

    // Define la estructura visual básica de la pantalla.
    Scaffold(
        // Barra superior de la pantalla.
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de la Rifa") },
                // Botón para navegar hacia atrás.
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        // Posición del botón de acción flotante.
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Filled.MoreVert, "Opciones")
                }
                // Menú desplegable que aparece al hacer clic en el botón de opciones.
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    // Elemento del menú para editar la rifa.
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        leadingIcon = {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar Rifa")
                        },
                        onClick = {
                            expanded = false
                            // Navega a la pantalla de edición de la rifa con el ID de la rifa actual.
                            rifaDetalle?.let { onNavigateToEditRifa(it.id) }
                        }
                    )
                    // Elemento del menú para eliminar la rifa.
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        onClick = {
                            expanded = false
                            mostrarDialogoEliminar = true
                        }
                    )
                }
            }
        },
        // Contenido principal de la pantalla.
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Muestra los detalles de la rifa si la información está disponible.
                rifaDetalle?.let {
                    // Información básica de la rifa.
                    Text("Nombre: ${it.nombre}", style = MaterialTheme.typography.headlineSmall)
                    Text("Descripción: ${it.descripcion}", style = MaterialTheme.typography.bodyLarge)
                    Text("Precio por boleto: $${it.valorUnitario}", style = MaterialTheme.typography.bodyMedium)
                    Text("Total de boletos: ${it.cantidadBoletos}", style = MaterialTheme.typography.bodyMedium)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val fechaSorteo = Date(it.fechaSorteo)
                    Text("Fecha de sorteo: ${sdf.format(fechaSorteo)}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sección para ingresar un número de boleto manualmente.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = numeroIngresado,
                            onValueChange = { value ->
                                // Permite solo dígitos y verifica que el número esté dentro del rango de boletos.
                                if (value.all { it.isDigit() } && (value.isEmpty() || value.toIntOrNull()?.let { num -> num in 1..it.cantidadBoletos } == true)) {
                                    numeroIngresado = value
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Número de boleto") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Botón para marcar el número ingresado como ocupado.
                        Button(
                            onClick = {
                                numeroIngresado.toIntOrNull()?.let { numero ->
                                    // Verifica que el número sea válido y no esté ya ocupado localmente.
                                    if (numero in 1..it.cantidadBoletos && !ocupadosLocal.contains(numero)) {
                                        viewModel.toggleNumeroOcupado(rifaId, numero)
                                        ocupadosLocal.add(numero)
                                        numeroIngresado = ""
                                    }
                                }
                            },
                            // El botón se habilita solo si hay un número ingresado válido y no está ocupado.
                            enabled = numeroIngresado.isNotEmpty() && numeroIngresado.toIntOrNull() != null && !ocupadosLocal.contains(numeroIngresado.toIntOrNull())
                        ) {
                            Text("Marcar")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sección para la matriz de números de boletos.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selecciona un número",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Indicador visual para los boletos ocupados.
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ocupado",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Muestra la cuadrícula de números de boletos.
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 56.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        items((1..it.cantidadBoletos).toList()) { numero ->
                            // Verifica si el número actual está en la lista local de ocupados.
                            val estaOcupadoLocal = ocupadosLocal.contains(numero)
                            NumeroBoleto(
                                numero = numero,
                                estaOcupado = estaOcupadoLocal,
                            )
                        }
                    }

                    // Sección que muestra el resumen de los boletos seleccionados.
                    if (ocupadosLocal.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Boletos seleccionados: ${ocupadosLocal.size}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Total a pagar: $${ocupadosLocal.size * it.valorUnitario}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Números: ${ocupadosLocal.sorted().joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para iniciar el sorteo.
                    Button(
                        onClick = {
                            // Llama a la función del ViewModel para realizar el sorteo.
                            viewModel.sortearRifa(rifaId) { ganador ->
                                // Actualiza el estado del número ganador con el resultado del sorteo.
                                numeroGanador = ganador
                            }
                        },
                        // El botón se habilita solo si hay boletos ocupados y aún no se ha determinado un ganador.
                        enabled = ocupadosLocal.isNotEmpty() && numeroGanador == null
                    ) {
                        Text("Sortear")
                    }

                    // Muestra el número ganador si ya se ha realizado el sorteo.
                    numeroGanador?.let { ganador ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "¡El número ganador es: $ganador!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    // Diálogo de confirmación para eliminar la rifa.
                    if (mostrarDialogoEliminar) {
                        AlertDialog(
                            onDismissRequest = { mostrarDialogoEliminar = false },
                            title = { Text("Confirmar Eliminación") },
                            text = { rifaDetalle?.let { Text("¿Estás seguro de que quieres eliminar la rifa \"${it.nombre}\"? Esta acción no se puede deshacer.") } },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        mostrarDialogoEliminar = false
                                        rifaDetalle?.let {
                                            // Llama a la función del ViewModel para eliminar la rifa.
                                            viewModel.eliminarRifa(it.id)
                                            Toast.makeText(context, "Rifa eliminada", Toast.LENGTH_SHORT).show()
                                            onNavigateBack()
                                        }
                                    }
                                ) {
                                    Text("Eliminar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { mostrarDialogoEliminar = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                } ?: run {
                    // Muestra un mensaje si no se encuentra la rifa con el ID proporcionado.
                    Text("Rifa no encontrada.")
                }
            }
        }
    )
}

@Composable
fun NumeroBoleto(
    numero: Int,
    estaOcupado: Boolean,
) {
    // Define el color de fondo de la tarjeta según si el boleto está ocupado o no.
    val backgroundColor = if (estaOcupado) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    // Define el color del contenido de la tarjeta según si el boleto está ocupado o no.
    val contentColor = if (estaOcupado) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // Composable que representa visualmente un único número de boleto.
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f), // Mantiene la proporción 1:1 para que sea un cuadrado.
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Borde de la tarjeta.
    ) {
        // Contenedor para centrar el contenido dentro de la tarjeta.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Icono de "check" que se muestra solo si el boleto está ocupado.
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = if (estaOcupado) "Número ocupado" else null,
                tint = contentColor,
                modifier = Modifier.alpha(if (estaOcupado) 1f else 0f) // Controla la transparencia del icono.
            )
            // Muestra el número del boleto si no está ocupado.
            if (!estaOcupado) {
                Text(
                    text = numero.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
