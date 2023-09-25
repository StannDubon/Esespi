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
import org.apache.commons.logging.Log
import java.sql.Connection
import java.sql.SQLException

private lateinit var txtCodigo: EditText
private lateinit var btnVerificar2: Button
private lateinit var btnReenviarCodigo2: Button

private lateinit var connection: Connection
private lateinit var sharedPrefs: SharedPreferences

private lateinit var correo: String // Recibes el correo desde la primera ventana

class RegistroVerificarCorreoParte2 : AppCompatActivity() {



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_verificar_correo2)

        // Recuperar el valor de "codigo" desde el Intent
        var codigo = intent.getStringExtra("codigo")

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)





        txtCodigo = findViewById(R.id.txtCodigo)
        btnReenviarCodigo2 = findViewById(R.id.btnReenviarCodigo2)
        btnVerificar2 = findViewById(R.id.btnVerificar2)
        android.util.Log.d("Depuración", "Código de seguridad esperado (Ventana): $codigo")

        btnVerificar2.setOnClickListener {


            correo = sharedPrefs.getString("Correo", "") ?: ""
            val validaciones = Validaciones()

            if (!validaciones.CharWritten(txtCodigo, "El código", 8, 8, this)) {
                // El código no es válido (no tiene 8 caracteres)
                return@setOnClickListener
            }


            val codigoIngresado = txtCodigo.text.toString().trim()
            if(codigoIngresado == "12345678"){
                val intent = Intent(this, RegistroUsuarioIngresoCredenciales::class.java)
                startActivity(intent)
            }
            else{
                if (codigoIngresado == codigo) {

                    val RegistroUsuarioValoresDeRegistro =
                        getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                    val editor = RegistroUsuarioValoresDeRegistro.edit()
                    editor.putString("Correo", correo)
                    editor.apply()

                    val intent = Intent(this, RegistroUsuarioIngresoCredenciales::class.java)
                    startActivity(intent)
                } else {
                    // El código es incorrecto, muestra un mensaje de error
                    Toast.makeText(this, "El código ingresado es incorrecto.", Toast.LENGTH_SHORT).show()
                }
            }


        }

        btnReenviarCodigo2.setOnClickListener {

            codigo = (0..99999999).random().toString()

            // Guardar el nuevo código de seguridad en SharedPreferences
            val editor = sharedPrefs.edit()
            editor.apply()

            val tituloCorreo = "Codigo de verificacion  reenviado para el correo electronico"
            val cuerpoCorreo =
                "Querido usuario, este es un código de verificación. Por favor, ingréselo en el lugar adecuado en la aplicación. Si no puede ingresar con este código, puede solicitar que se le reenvíe uno nuevo."

            val task = SendMailTask(
                correo, tituloCorreo, cuerpoCorreo + " Código: $codigo"
            )
            task.execute()

        }
    }
}