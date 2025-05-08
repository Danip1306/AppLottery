package com.example.lottery.data.model

data class Rifa(
    val id: Int, // Lo generaremos nosotros temporalmente
    val nombre: String,
    val cantidadBoletos: Int,
    val valorUnitario: Double,
    val fechaSorteo: Long // Timestamp de la fecha del sorteo (milisegundos desde Epoch)
)