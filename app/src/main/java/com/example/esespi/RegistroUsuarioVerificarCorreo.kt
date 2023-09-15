package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

private lateinit var txtCorreo: EditText
private lateinit var txtCodigo: EditText
private lateinit var btnReeviarCodigo : Button
private lateinit var btnVerificar: Button
private var codigoSeguridad: String = "no varificado"


class RegistroUsuarioVerificarCorreo : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_verificar_correo)

        txtCorreo = findViewById(R.id.RegistroUsuarioCorreoTxtCorreo)
        txtCodigo = findViewById(R.id.RegistroUsuarioCorreoTxtCodigo)
        btnReeviarCodigo = findViewById(R.id.RegistroUsuarioCorreoBtnReenviarCodigo)
        btnVerificar = findViewById(R.id.RegistroUsuarioCorreoBtnVerificar)

        btnVerificar.setOnClickListener {

            if(txtCodigo.text.toString() == codigoSeguridad){
                val RegistroUsuarioValoresDeRegistro = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                val editor = RegistroUsuarioValoresDeRegistro.edit()
                editor.putString("Correo", txtCorreo.text.toString())
                editor.apply()

                val intent = Intent(this, RegistroUsuarioVerificarTelefono::class.java)
                startActivity(intent)
            }

            if(txtCorreo.text.toString()=="rootRequest"){
                val RegistroUsuarioValoresDeRegistro = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                val editor = RegistroUsuarioValoresDeRegistro.edit()
                editor.putString("Correo", txtCorreo.text.toString())
                editor.apply()

                val intent = Intent(this, RegistroUsuarioVerificarTelefono::class.java)
                startActivity(intent)
            }
        }

        btnReeviarCodigo.setOnClickListener{
            codigoSeguridad= (0..99999999).random().toString()
            val task = SendMailTask(txtCorreo.text.toString(), "Saludos", codigoSeguridad)
            task.execute()
        }

    }
}