package com.example.lottery.data.model

data class Rifa(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val cantidadBoletos: Int,
    val valorUnitario: Double,
    val fechaSorteo: Long
)