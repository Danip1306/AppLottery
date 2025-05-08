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
import com.example.lottery.ui.screens.DetalleRifaScreen // Asegúrate de crear esta pantalla
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

@Composable
fun AppContent() {
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Principal) }
    var rifaIdDetalle by remember { mutableStateOf(-1) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (pantallaActual) {
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
                DetalleRifaScreen(
                    rifaId = rifaIdDetalle,
                    onNavigateBack = { pantallaActual = Pantalla.Principal }
                )
            }
        }
    }
}

enum class Pantalla {
    Principal,
    Añadir,
    Detalle
}