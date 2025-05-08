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
        rifasList.add(rifa)
        guardarListaRifas(rifasList)
    }

    fun eliminarRifa(rifaId: Int) {
        val rifasList = obtenerTodasLasRifas().filter { it.id != rifaId }
        guardarListaRifas(rifasList)
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
}