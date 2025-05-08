package com.example.lottery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lottery.data.RifaPreferences
import com.example.lottery.data.model.Rifa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RifaViewModel(private val rifaPreferences: RifaPreferences) : ViewModel() {

    private val _rifas = MutableStateFlow<List<Rifa>>(emptyList())
    val rifas: StateFlow<List<Rifa>> = _rifas

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    init {
        cargarRifasIniciales()
    }

    fun actualizarTextoBusqueda(texto: String) {
        _searchText.value = texto
        buscarRifas(texto)
    }

    private fun cargarRifasIniciales() {
        _rifas.value = rifaPreferences.obtenerTodasLasRifas()
    }

    private fun buscarRifas(query: String) {
        _rifas.update {
            rifaPreferences.obtenerTodasLasRifas().filter {
                it.nombre.contains(query, ignoreCase = true)
            }
        }
    }

    fun insertarRifa(nombre: String, cantidadBoletos: Int, valorUnitario: Double, fechaSorteo: Long) {
        viewModelScope.launch {
            val nuevaRifa = Rifa(
                id = generarIdUnico(), // Generamos un ID único temporal
                nombre = nombre,
                cantidadBoletos = cantidadBoletos,
                valorUnitario = valorUnitario,
                fechaSorteo = fechaSorteo
            )
            rifaPreferences.guardarRifa(nuevaRifa)
            cargarRifasIniciales() // Volvemos a cargar la lista para actualizar la UI
        }
    }

    fun eliminarRifa(rifaId: Int) {
        viewModelScope.launch {
            rifaPreferences.eliminarRifa(rifaId)
            cargarRifasIniciales()
        }
    }

    // Función temporal para generar IDs únicos (puedes usar timestamps o un contador)
    private fun generarIdUnico(): Int {
        return System.currentTimeMillis().hashCode()
    }

    class RifaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RifaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RifaViewModel(RifaPreferences(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}