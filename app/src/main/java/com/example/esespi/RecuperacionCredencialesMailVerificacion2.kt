package com.example.esespi


import android.annotation.SuppressLint
import android.content.Context
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
private lateinit var btnReenviarCodigo2: Button

private lateinit var connection: Connection
private lateinit var sharedPrefs: SharedPreferences

private lateinit var codigoSeguridad: String
private lateinit var correo: String // Recibes el correo desde la primera ventana
var correo_usuario_a_recuperar:String=""
class activity_recuperacion_credenciales_mail_verificacion : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_verificar_correo2)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
        correo = sharedPrefs.getString("Correo", "") ?: ""
        codigoSeguridad = sharedPrefs.getString("Codigo", "") ?: ""

        txtCodigo = findViewById(R.id.txtCodigo)
        btnReenviarCodigo2 = findViewById(R.id.recuperacionCredencialesMailVerificacionBtnReenviarCodigo)

        btnVerificar2 = findViewById(R.id.recuperacionCredencialesMailVerificacionBtnVerificar2)
        android.util.Log.d("Depuración", "Código de seguridad esperado (Ventana): $codigoSeguridad")

        btnVerificar2.setOnClickListener {

            val validaciones = Validaciones()

            if (!validaciones.CharWritten(txtCodigo, "El código", 8, 8, this)) {
                // El código no es válido (no tiene 8 caracteres)
                return@setOnClickListener
            }
            val codigoIngresado = txtCodigo.text.toString().trim()

            if (codigoIngresado == codigoSeguridad) {

                val RegistroUsuarioValoresDeRegistro =
                    getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                val editor = RegistroUsuarioValoresDeRegistro.edit()
                editor.putString("Correo", correo)
                editor.apply()
                correo_usuario_a_recuperar=correo

                //val intent = Intent(this, RegistroUsuarioVerificarTelefono::class.java)
                //startActivity(intent)
            } else {
                // El código es incorrecto, muestra un mensaje de error
                Toast.makeText(this, "El código ingresado es incorrecto.", Toast.LENGTH_SHORT).show()
            }
        }

        btnReenviarCodigo2.setOnClickListener {

            codigoSeguridad = (0..99999999).random().toString()

            // Guardar el nuevo código de seguridad en SharedPreferences
            val editor = sharedPrefs.edit()
            editor.putString("Codigo", codigoSeguridad)
            editor.apply()

            val tituloCorreo = "Codigo de verificacion  reenviado para el correo electronico"
            val cuerpoCorreo =
                "Querido usuario, este es un código de verificación. Por favor, ingréselo en el lugar adecuado en la aplicación. Si no puede ingresar con este código, puede solicitar que se le reenvíe uno nuevo."

            val task = SendMailTask(
                correo, tituloCorreo, cuerpoCorreo + " Código: $codigoSeguridad"
            )
            task.execute()

        }
    }
}