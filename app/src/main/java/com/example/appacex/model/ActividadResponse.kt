package com.example.appacex.model

data class ActividadResponse(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val fini: String,
    val ffin: String,
    val hini: String,
    val hfin: String,
    val estado: String,
    val solicitante: Solicitante
)

data class Solicitante(
    val uuid: String,
    val nombre: String,
    val apellidos: String,
    val correo: String
)