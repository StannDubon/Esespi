package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.SQLException

private lateinit var txtCodigo: EditText
private lateinit var btnVerificar2: Button
private lateinit var btnReenviar: Button

private lateinit var connection: Connection
private lateinit var sharedPrefs: SharedPreferences
var nuevoCodigo:String = ""

class RecuperacionCredencialesMailVerificacion2 : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_mail_verificacion2)

        // Recuperar el valor de "codigo" desde el Intent
        val codigo = intent.getStringExtra("codigo")

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)

        // Recuperar el correo almacenado en SharedPreferences
        val correo = sharedPrefs.getString("Correo", "") ?: ""

        txtCodigo = findViewById(R.id.recuperacionCredencialesMailVerificacionTxtCodigo)
        btnReenviar = findViewById(R.id.recuperacionCredencialesMailVerificacionBtnReenviarCodigo)
        btnVerificar2 = findViewById(R.id.btnVerificarCodigo)

        android.util.Log.d("Depuración", "Código de seguridad esperado (Ventana): $codigo")

        btnVerificar2.setOnClickListener {
            val validaciones = Validaciones()
            val codigoIngresado = txtCodigo.text.toString().trim()

            if (validaciones.CharWritten(txtCodigo, "El código", 8, 8, this)) {
                if (codigoIngresado == codigo || codigoIngresado ==nuevoCodigo) {
                    // Código correcto, continuar con la lógica
                    val RegistroUsuarioValoresDeRegistro = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                    val editor = RegistroUsuarioValoresDeRegistro.edit()
                    editor.putString("Correo", correo)
                    editor.apply()

                    val intent = Intent(this, RecuperacionCredencialesRecuperarContrasena::class.java)
                    startActivity(intent)
                } else {
                    // El código es incorrecto, muestra un mensaje de error
                    Toast.makeText(this, "El código ingresado es incorrecto.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnReenviar.setOnClickListener {
            // Generar un nuevo código de seguridad
            nuevoCodigo = (0..99999999).random().toString()

            // Guardar el nuevo código en SharedPreferences
            val editor = sharedPrefs.edit()
            editor.putString("Codigo", nuevoCodigo)
            editor.apply()

            // Enviar el nuevo código por correo electrónico
            val tituloCorreo = "Código de verificación reenviado para el correo electrónico"
            val cuerpoCorreo =
                "Querido usuario, este es un código de verificación. Por favor, ingréselo en la aplicación. Si no puede ingresar con este código, puede solicitar que se le reenvíe uno nuevo."

            val task = SendMailTask(correo, tituloCorreo, cuerpoCorreo + " Código: $nuevoCodigo")
            task.execute()
        }
    }
}
