package com.example.esespi

import android.content.Context
import android.os.Handler
import android.widget.EditText
import android.widget.Toast

class Validaciones {

    fun CharWritten(EditText: EditText, NombreCampo: String, Longitud: Int, Contexto: Context): Boolean {
        val texto = EditText.text.toString().trim()
        var resultado = false
        if (texto.length != Longitud) {
            Toast.makeText(Contexto, "$NombreCampo requiere $Longitud caracteres", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                resultado = false
            }, 500)
        } else {
            resultado = true
        }
        return resultado
    }

    fun obtenerNumeroMes(nombreMes: String): Int {
        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return meses.indexOfFirst { it.equals(nombreMes, ignoreCase = true) } + 1
    }

    fun FechaReal(dia: Int, mes: Int, año: Int, contexto: Context): Boolean {
        var isReal = true

        if (año <= 0 || mes < 1 || mes > 12 || dia < 1 || dia > 31) {
            isReal = false
        } else {
            when (mes) {
                2 -> {
                    // Febrero
                    if ((año % 4 == 0 && año % 100 != 0) || (año % 400 == 0)) {
                        // Año bisiesto
                        if (dia > 29) {
                            isReal = false
                        }
                    } else {
                        if (dia > 28) {
                            isReal = false
                        }
                    }
                }
                4, 6, 9, 11 -> {
                    // Abril, Junio, Septiembre, Noviembre
                    if (dia > 30) {
                        isReal = false
                    }
                }
                else -> {
                    // Otros meses
                    if (dia > 31) {
                        isReal = false
                    }
                }
            }
        }

        if (isReal) {Toast.makeText(contexto, "Fecha válida", Toast.LENGTH_SHORT).show()}
        else {Toast.makeText(contexto, "Fecha no válida", Toast.LENGTH_SHORT).show()}
        return isReal
    }


}