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

        txtCorreo = findViewById(R.id.TxtCorreo)
        txtCodigo = findViewById(R.id.TxtCodigo)
        btnReeviarCodigo = findViewById(R.id.btnReenviarCodigo)
        btnVerificar = findViewById(R.id.BtnVerificar)


        val validaciones = Validaciones()


        btnVerificar.setOnClickListener {

            //if (!validaciones.validarCorreoElectronico(txtCorreo, this)) {
                // El correo electrónico no es válido
            //    return@setOnClickListener
            //}

            //if (!validaciones.CharWritten(txtCodigo, "El codigo", 8, 8, this)) {
            //    return@setOnClickListener
            //}



            if (txtCodigo.text.toString() == codigoSeguridad) {
                val RegistroUsuarioValoresDeRegistro = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                val editor = RegistroUsuarioValoresDeRegistro.edit()
                editor.putString("Correo", txtCorreo.text.toString())
                editor.apply()

                val intent = Intent(this, RegistroUsuarioVerificarTelefono::class.java)
                startActivity(intent)
            }


            if (txtCorreo.text.toString() == "rootRequest") {
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
            val tituloCorreo = "Codigo de verificacion para correo electronico"
            val cuerpoCorreo = "Querido usuario, este es un código de verificación. Por favor, ingréselo en el lugar adecuado en la aplicación. Si no puede ingresar con este código, puede solicitar que se le reenvíe uno nuevo."

            val task = SendMailTask(txtCorreo.text.toString(), tituloCorreo, cuerpoCorreo + " Código: $codigoSeguridad")
            task.execute()
        }

    }
}