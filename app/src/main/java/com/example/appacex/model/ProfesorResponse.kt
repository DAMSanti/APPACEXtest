package com.example.appacex.model

data class ProfesorResponse(
    val uuid: String,
    val dni: String,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    val rol: String,
    val activo: Int,
    val urlFoto: String?,
    val esJefeDep: Int,
    val depart: Departamento
)

data class Departamento(
    val id: Int,
    val codigo: String,
    val nombre: String
)