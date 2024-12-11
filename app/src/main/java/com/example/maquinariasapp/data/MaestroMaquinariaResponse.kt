package com.example.maquinariasapp.data

data class MaestroMaquinariaResponse(
    val id: String,
    val codigo: String,
    val nombre: String,
    val ubicacion: String,
    val estadoDeRegistro: String,
    val lineaDeProduccion: LineaProduccion,
    val plantaDeProduccion: PlantaProduccion
)
