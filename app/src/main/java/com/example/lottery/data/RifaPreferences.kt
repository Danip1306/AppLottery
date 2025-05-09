package com.example.lottery.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.lottery.data.model.Rifa

class RifaPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("rifas_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun guardarRifa(rifa: Rifa) {
        val rifasList = obtenerTodasLasRifas().toMutableList()

        // Buscar si ya existe una rifa con el mismo ID
        val index = rifasList.indexOfFirst { it.id == rifa.id }

        if (index != -1) {
            // Si existe, reemplazarla
            rifasList[index] = rifa
        } else {
            // Si no existe, añadirla y inicializar números ocupados
            rifasList.add(rifa)
            // Solo inicializamos los números ocupados para rifas nuevas
            guardarNumerosOcupados(rifa.id, emptyList())
            // Inicializamos el ganador como nulo para rifas nuevas
            guardarNumeroGanador(rifa.id, null)
        }

        // Guardar la lista actualizada
        guardarListaRifas(rifasList)
    }

    fun eliminarRifa(rifaId: Int) {
        val rifasList = obtenerTodasLasRifas().filter { it.id != rifaId }
        guardarListaRifas(rifasList)

        // Eliminamos los números ocupados de esta rifa
        eliminarNumerosOcupados(rifaId)
        // Eliminamos el ganador de esta rifa
        eliminarNumeroGanador(rifaId)
    }

    fun obtenerRifaPorId(rifaId: Int): Rifa? {
        return obtenerTodasLasRifas().find { it.id == rifaId }
    }

    fun obtenerTodasLasRifas(): List<Rifa> {
        val json = sharedPreferences.getString("rifas", null)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Rifa>>() {}.type
            gson.fromJson(json, type)
        }
    }

    private fun guardarListaRifas(rifasList: List<Rifa>) {
        val json = gson.toJson(rifasList)
        sharedPreferences.edit().putString("rifas", json).apply()
    }

    // Métodos para manejar los números ocupados

    //Guarda la lista de números ocupados para una rifa específica

    fun guardarNumerosOcupados(rifaId: Int, numeros: List<Int>) {
        val key = "numeros_ocupados_$rifaId"
        val json = gson.toJson(numeros)
        sharedPreferences.edit().putString(key, json).apply()
    }

    //Elimina los números ocupados de una rifa

    fun eliminarNumerosOcupados(rifaId: Int) {
        val key = "numeros_ocupados_$rifaId"
        sharedPreferences.edit().remove(key).apply()
    }

    //Obtiene la lista de números ocupados para una rifa específica

    fun obtenerNumerosOcupados(rifaId: Int): List<Int> {
        val key = "numeros_ocupados_$rifaId"
        val json = sharedPreferences.getString(key, null)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(json, type)
        }
    }

    // Métodos para manejar el número ganador

    //Guarda el número ganador para una rifa específica
    fun guardarNumeroGanador(rifaId: Int, numeroGanador: Int?) {
        val key = "ganador_$rifaId"
        sharedPreferences.edit().apply {
            if (numeroGanador == null) {
                remove(key)
            } else {
                putInt(key, numeroGanador)
            }
        }.apply()
    }

    //Obtiene el número ganador de una rifa específica

    fun obtenerNumeroGanador(rifaId: Int): Int? {
        val key = "ganador_$rifaId"
        val ganador = sharedPreferences.getInt(key, -1) // -1 indica que no hay ganador guardado o la clave no existe
        val resultado = if (ganador == -1) null else ganador
        return resultado
    }

    //Elimina el número ganador de una rifa

    fun eliminarNumeroGanador(rifaId: Int) {
        val key = "ganador_$rifaId"
        sharedPreferences.edit().remove(key).apply()
    }
}