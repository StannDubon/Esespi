package com.example.esespi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

private lateinit var txtEmail:EditText
private lateinit var txtCodigo:EditText
private lateinit var btnReenviar:Button
private lateinit var btnConfirmar:Button
private var codigoSeguridad: String = "no varificado"
var correo_usuario_a_recuperar:String=""

class RecuperacionCredencialesMailVerificacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_mail_verificacion)

        txtEmail=findViewById(R.id.RecuperacionCredencialesMailVerificacionTxtEmail)
        txtCodigo=findViewById(R.id.RecuperacionCredencialesMailVerificacionTxtCodigo)
        btnReenviar=findViewById(R.id.RecuperacionCredencialesMailVerificacionBtnReenviarCodigo)
        btnConfirmar=findViewById(R.id.RecuperacionCredencialesMailVerificacionBtnVerificar)


        btnConfirmar.setOnClickListener {

            if(txtCodigo.text.toString() == codigoSeguridad){
                val RegistroUsuarioValoresDeRegistro = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                val editor = RegistroUsuarioValoresDeRegistro.edit()
                editor.putString("Correo", txtEmail.text.toString())
                editor.apply()
                correo_usuario_a_recuperar=txtEmail.text.toString()

                val intent = Intent(this, RecuperacionCredencialesElegirCredencial::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnReenviar.setOnClickListener{
            codigoSeguridad= (0..99999999).random().toString()
            val task = SendMailTask(txtEmail.text.toString(), "Saludos", codigoSeguridad)
            task.execute()
        }
    }
}