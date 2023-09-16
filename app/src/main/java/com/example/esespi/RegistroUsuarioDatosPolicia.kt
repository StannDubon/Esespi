package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

private lateinit var txtONI: EditText
private lateinit var txtNumeroPlaca: EditText
private lateinit var btnSiguiente: Button

class RegistroUsuarioDatosPolicia : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_datos_policia)

        txtONI=findViewById(R.id.RegistroUsuarioDatosPoliciaONI)
        txtNumeroPlaca=findViewById(R.id.RegistroUsuarioDatosPoliciaNumeroPlaca)
        btnSiguiente=findViewById(R.id.RegistroUsuarioDatosPoliciaSiguiente)

        btnSiguiente.setOnClickListener {
            val RegistroUsuarioValoresDeRegistro =
                getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
            val editor = RegistroUsuarioValoresDeRegistro.edit()
            editor.putString("ONI", txtONI.text.toString())
            editor.putString("NumeroPlaca", txtNumeroPlaca.text.toString())
            editor.apply()

            val intent = Intent(this, RegistroUsuarioVerificarCorreo::class.java)
            startActivity(intent)
        }
    }
}