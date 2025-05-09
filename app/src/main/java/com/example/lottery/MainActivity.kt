package com.example.lottery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.lottery.ui.screens.AñadirRifaScreen
import com.example.lottery.ui.screens.DetalleRifaScreen
import com.example.lottery.ui.screens.EditarRifaScreen
import com.example.lottery.ui.screens.PantallaPrincipal
import com.example.lottery.ui.theme.LotteryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LotteryTheme {
                AppContent()
            }
        }
    }
}
//gestiona la navegación entre diferentes pantallas de la aplicación.
@Composable
fun AppContent() {
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Principal) }
    var rifaIdDetalle by remember { mutableStateOf(-1) }
    var rifaIdEditar by remember { mutableStateOf(-1) }

    // actualización de la pantalla de detalles después de editar
    var detalleUpdateKey by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (pantallaActual) {
            // Se le pasan lambdas para manejar la navegación a la pantalla de detalles y a la pantalla de añadir rifa.
            Pantalla.Principal -> {
                PantallaPrincipal(
                    onNavigateToDetalle = { id ->
                        rifaIdDetalle = id
                        pantallaActual = Pantalla.Detalle
                    },
                    onNavigateToAddRifa = {
                        pantallaActual = Pantalla.Añadir
                    }
                )
            }
            Pantalla.Añadir -> {
                AñadirRifaScreen(onNavigateBack = { pantallaActual = Pantalla.Principal })
            }
            Pantalla.Detalle -> {
                // Usar la clave de actualización para forzar la recomposición
                key(rifaIdDetalle, detalleUpdateKey) {
                    DetalleRifaScreen(
                        rifaId = rifaIdDetalle,
                        onNavigateBack = { pantallaActual = Pantalla.Principal },
                        onNavigateToEditRifa = { id ->
                            rifaIdEditar = id
                            pantallaActual = Pantalla.Editar
                        }
                    )
                }
            }
            Pantalla.Editar -> {
                EditarRifaScreen(
                    rifaId = rifaIdEditar,
                    onNavigateBack = {
                        // Incrementar la clave para forzar la actualización de la pantalla de detalles
                        detalleUpdateKey++
                        pantallaActual = Pantalla.Detalle
                    }
                )
            }
        }
    }
}
//define los diferentes estados o pantallas de la aplicación.
enum class Pantalla {
    Principal,
    Añadir,
    Detalle,
    Editar
}