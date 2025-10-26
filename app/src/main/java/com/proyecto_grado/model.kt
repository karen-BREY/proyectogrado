package com.proyecto_grado

import java.io.Serializable as serializable

data class Alimento(
    val id: Int = 0,
    val nombre: String,
    val cantidad: String,
    val fechaIngreso: String,
    val fechaVencimiento: String,
    val tipo: String
)

data class Animal(
    val id: Int,
    val nombre: String,
    val numero: Int,
    val lote: Int,
    val fechaNacimiento: String,
    val edad: String,
    val raza: String,
    val pesoActual: Double,
    val observaciones: String
)
data class Lote(
    val idLote: Int = 0, // A default value is useful, especially for new lots
    val numero: Int,
    val observacion: String
)

data class ReporteGeneral(
    val nombreAnimal: String,
    val raza: String,
    val fechaNacimiento: String,
    val pesoActual: Double,
    val alimento: String,
    val observacion: String
) : serializable

data class Usuario(
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val telefono: String = "",
    val contrasena: String = ""
)