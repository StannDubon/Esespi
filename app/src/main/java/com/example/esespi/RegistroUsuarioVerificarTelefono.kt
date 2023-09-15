package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

private lateinit var txtTelefono: EditText
private lateinit var txtCodigo: EditText
private lateinit var btnReeviarCodigo : Button
private lateinit var btnVerificar: Button

class RegistroUsuarioVerificarTelefono : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_verificar_telefono)

        txtTelefono = findViewById(R.id.RegistroUsuarioTelefonoTxtTelefono)
        txtCodigo = findViewById(R.id.RegistroUsuarioTelefonoTxtCodigo)
        btnReeviarCodigo = findViewById(R.id.RegistroUsuarioTelefonoBtnReenviarCodigo)
        btnVerificar = findViewById(R.id.RegistroUsuarioTelefonoBtnVerificar)

        btnVerificar.setOnClickListener {
            val RegistroUsuarioValoresDeRegistro = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
            val editor = RegistroUsuarioValoresDeRegistro.edit()
            editor.putString("Telefono", txtTelefono.text.toString())
            editor.apply()

            val intent = Intent(this, RegistroUsuarioReferenciasPersonales::class.java)
            startActivity(intent)
        }

        btnReeviarCodigo.setOnClickListener{

        }
    }
}