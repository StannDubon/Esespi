package com.example.esespi

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun obtenerHoraActual(): String {
    val formato = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val horaActual = Date()
    return formato.format(horaActual)
}

fun main() {
    val horaFormateada = obtenerHoraActual()
    println("Hora actual formateada: $horaFormateada")
}