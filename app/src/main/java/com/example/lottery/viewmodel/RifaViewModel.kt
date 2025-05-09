package com.example.lottery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lottery.data.RifaPreferences
import com.example.lottery.data.model.Rifa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class RifaViewModel(private val rifaPreferences: RifaPreferences) : ViewModel() {

    // Almacena la lista de rifas y notifica los cambios en la interfaz de usuario.
    private val _rifas = MutableStateFlow<List<Rifa>>(emptyList())
    val rifas: StateFlow<List<Rifa>> = _rifas
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    private val _numerosOcupados = MutableStateFlow<Map<Int, List<Int>>>(emptyMap())
    val numerosOcupados: StateFlow<Map<Int, List<Int>>> = _numerosOcupados.asStateFlow()
    private val _numeroGanador = MutableStateFlow<Map<Int, Int?>>(emptyMap())
    val numeroGanador: StateFlow<Map<Int, Int?>> = _numeroGanador.asStateFlow()

    init {
        cargarRifasIniciales()
        inicializarNumerosOcupados()
        inicializarNumerosGanadores()
    }

    // Carga los números ganadores guardados desde las preferencias para cada rifa.
    private fun inicializarNumerosGanadores() {
        val ganadoresIniciales = mutableMapOf<Int, Int?>()
        rifaPreferences.obtenerTodasLasRifas().forEach { rifa ->
            ganadoresIniciales[rifa.id] = rifaPreferences.obtenerNumeroGanador(rifa.id)
        }
        _numeroGanador.value = ganadoresIniciales
    }

    // Carga los números ocupados guardados desde las preferencias para cada rifa.
    private fun inicializarNumerosOcupados() {
        val numerosInicialesOcupados = mutableMapOf<Int, List<Int>>()
        rifaPreferences.obtenerTodasLasRifas().forEach { rifa ->
            numerosInicialesOcupados[rifa.id] = rifaPreferences.obtenerNumerosOcupados(rifa.id)
        }
        _numerosOcupados.value = numerosInicialesOcupados
    }

    //Cambia el estado de un número (ocupado/no ocupado) para una rifa y guarda el cambio.

    fun toggleNumeroOcupado(rifaId: Int, numero: Int) {
        val numerosActuales = _numerosOcupados.value[rifaId]?.toMutableList() ?: mutableListOf()
        val estaOcupado = numero in numerosActuales

        if (estaOcupado) {
            numerosActuales.remove(numero)
        } else {
            numerosActuales.add(numero)
        }

        _numerosOcupados.update {
            it + (rifaId to numerosActuales.toList())
        }

        // Guarda la lista actualizada de números ocupados en las preferencias.
        guardarNumerosOcupados(rifaId, numerosActuales.toList())
    }

    //Guarda la lista de números ocupados para una rifa específica en las preferencias.

    private fun guardarNumerosOcupados(rifaId: Int, numeros: List<Int>) {
        rifaPreferences.guardarNumerosOcupados(rifaId, numeros)
    }

    //Verifica si un número específico está marcado como ocupado para una rifa.

    fun isNumeroOcupado(rifaId: Int, numero: Int): Boolean {
        return _numerosOcupados.value[rifaId]?.contains(numero) ?: false
    }

    //Obtiene la lista de todos los números que están marcados como ocupados para una rifa.

    fun getNumerosOcupados(rifaId: Int): List<Int> {
        return _numerosOcupados.value[rifaId] ?: emptyList()
    }

    //Realiza el sorteo de una rifa seleccionando un número aleatorio de los números ocupados y lo guarda.

    fun sortearRifa(rifaId: Int, onSorteoRealizado: (Int?) -> Unit) {
        val numerosOcupadosParaRifa = _numerosOcupados.value[rifaId]

        if (numerosOcupadosParaRifa.isNullOrEmpty()) {
            // Si no hay números ocupados, no se puede realizar el sorteo.
            onSorteoRealizado(null)
            _numeroGanador.update { it - rifaId }
            guardarNumeroGanador(rifaId, null)
        } else {
            // Selecciona un número aleatorio de la lista de números ocupados.
            val ganador = numerosOcupadosParaRifa.random()
            _numeroGanador.update { it + (rifaId to ganador) }
            guardarNumeroGanador(rifaId, ganador)
            onSorteoRealizado(ganador)
        }
    }

    //Guarda el número ganador de una rifa específica en las preferencias.

    private fun guardarNumeroGanador(rifaId: Int, numeroGanador: Int?) {
        rifaPreferences.guardarNumeroGanador(rifaId, numeroGanador)
    }

    //Obtiene el número ganador de una rifa específica.

    fun obtenerNumeroGanador(rifaId: Int): Int? {
        return _numeroGanador.value[rifaId]
    }

    // Actualiza el texto de búsqueda y filtra la lista de rifas según el nuevo texto.

    fun actualizarTextoBusqueda(texto: String) {
        _searchText.value = texto
        buscarRifas(texto)
    }

    // Carga la lista inicial de rifas desde las preferencias.
    private fun cargarRifasIniciales() {
        _rifas.value = rifaPreferences.obtenerTodasLasRifas()
    }

    // Filtra la lista de rifas según el texto de búsqueda proporcionado.
    private fun buscarRifas(query: String) {
        _rifas.update {
            rifaPreferences.obtenerTodasLasRifas().filter {
                it.nombre.contains(query, ignoreCase = true)
            }
        }
    }

    //Inserta una nueva rifa en las preferencias y actualiza la lista de rifas.

    fun insertarRifa(nombre: String, descripcion: String, cantidadBoletos: Int, valorUnitario: Double, fechaSorteo: Long) {
        viewModelScope.launch {
            val nuevaRifa = Rifa(
                id = generarIdUnico(),
                nombre = nombre,
                descripcion = descripcion,
                cantidadBoletos = cantidadBoletos,
                valorUnitario = valorUnitario,
                fechaSorteo = fechaSorteo
            )
            rifaPreferences.guardarRifa(nuevaRifa)
            cargarRifasIniciales()

            // Inicializa el estado de números ocupados y ganador para la nueva rifa.
            _numerosOcupados.update {
                it + (nuevaRifa.id to rifaPreferences.obtenerNumerosOcupados(nuevaRifa.id))
            }
            _numeroGanador.update {
                it + (nuevaRifa.id to null) // Inicializar sin ganador
            }
        }
    }

    //Elimina una rifa específica de las preferencias y actualiza la lista de rifas.

    fun eliminarRifa(rifaId: Int) {
        viewModelScope.launch {
            rifaPreferences.eliminarRifa(rifaId)
            rifaPreferences.eliminarNumerosOcupados(rifaId)
            rifaPreferences.eliminarNumeroGanador(rifaId)
            cargarRifasIniciales()
            // Elimina el estado de números ocupados y ganador para la rifa eliminada.
            _numerosOcupados.update { it - rifaId }
            _numeroGanador.update { it - rifaId }
        }
    }

    // Genera un ID único para una nueva rifa.
    private fun generarIdUnico(): Int {
        return System.currentTimeMillis().hashCode()
    }

    //Actualiza los detalles de una rifa existente en las preferencias y actualiza la lista de rifas.

    fun actualizarRifa(
        id: Int,
        nombre: String,
        descripcion: String,
        cantidadBoletos: Int,
        valorUnitario: Double,
        fechaSorteo: Long
    ) {
        viewModelScope.launch {
            val rifaActualizada = Rifa(
                id = id,
                nombre = nombre,
                descripcion = descripcion,
                cantidadBoletos = cantidadBoletos,
                valorUnitario = valorUnitario,
                fechaSorteo = fechaSorteo
            )
            rifaPreferences.guardarRifa(rifaActualizada) // Reutiliza la función guardar ya que sobrescribirá por ID
            cargarRifasIniciales()
        }
    }

    // Factory para crear instancias de RifaViewModel con la dependencia de RifaPreferences.
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