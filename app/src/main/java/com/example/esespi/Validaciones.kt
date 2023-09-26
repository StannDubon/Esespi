package com.example.esespi

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

class Validaciones {

    fun parsearFecha(inputFecha: String): String {
        try {
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

            val fecha = formatoEntrada.parse(inputFecha)

            if (fecha != null) {
                return formatoSalida.format(fecha)
            } else {
                // La cadena no pudo ser parseada correctamente
                return "Fecha no válida"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar la excepción si ocurre un error de parseo
            return "Error de parseo"
        }
    }

    fun CharWritten(EditText: EditText, NombreCampo: String, LongitudMax: Int, LongitudMin: Int, Contexto: Context): Boolean {
        val texto = EditText.text.toString().trim()
        var resultado = false
        if (texto.length < LongitudMin) {
            Toast.makeText(Contexto, "$NombreCampo requiere almenos $LongitudMin caracteres", Toast.LENGTH_SHORT).show()
        }
        else if(texto.length > LongitudMax){
            Toast.makeText(Contexto, "$NombreCampo acepta hasta $LongitudMax caracteres", Toast.LENGTH_SHORT).show()
        }
        else {
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

    fun GenderSelected(genero: Int, Contexto:Context): Boolean {
        var value = false
        if(genero==1 || genero==2){
            value=true
        }
        else{
            Toast.makeText(Contexto, "Por favor seleccione el genero", Toast.LENGTH_SHORT).show()
            value=false
        }
        return value
    }

    fun PictureSelected(foto: ByteArray, Contexto:Context): Boolean {
        var value = false
        if(foto!=null){
            value=true
        }
        else{
            Toast.makeText(Contexto, "Por favor ingrese una foto", Toast.LENGTH_SHORT).show()
            value=false
        }
        return value
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

        if (isReal == false) {Toast.makeText(contexto, "Fecha no válida", Toast.LENGTH_SHORT).show()}
        return isReal
    }

    fun validarCorreoElectronico(emailEditText: EditText, contexto: Context): Boolean {
        val email = emailEditText.text.toString().trim()

        // Patrón de expresión regular para validar correos electrónicos
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)

        if (!matcher.matches()) {
            Toast.makeText(contexto, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    fun validarDUI(duiEditText: EditText, contexto: Context): Boolean {
        val dui = duiEditText.text.toString().trim()

        // Patrón de expresión regular para validar el formato del DUI
        val duiPattern = "\\d{8}-\\d"

        val pattern = Pattern.compile(duiPattern)
        val matcher = pattern.matcher(dui)

        if (!matcher.matches()) {
            Toast.makeText(contexto, "DUI no válido. El formato correcto es XXXXXXXX-Y.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }



}