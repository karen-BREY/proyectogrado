package com.proyecto_grado

data class Alimento(
    val nombre: String = "",
    val cantidad: String = "",
    val fechaIngreso: String = "",
    val fechaVencimiento: String = "",
    val tipo: String = ""
)

data class Animal(
    val nombre: String = "",
    val raza: String = "",
    val fechaNacimiento: String = "",
    val peso: Double = 0.0,
    val observaciones: String = ""
)