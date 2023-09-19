package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.SQLException
import android.content.SharedPreferences

private lateinit var txtCorreo: EditText

private lateinit var btnVerificar: Button
private lateinit var connection: Connection

private var codigoSeguridad: String = "no varificado"
private lateinit var sharedPrefs: SharedPreferences


class RegistroUsuarioVerificarCorreo : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_verificar_correo)
        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        txtCorreo = findViewById(R.id.TxtCorreo)
        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)


        btnVerificar = findViewById(R.id.BtnVerificar)



        btnVerificar.setOnClickListener {
            val correo = txtCorreo.text.toString().trim()
            val validaciones = Validaciones()

            if (!validaciones.validarCorreoElectronico(txtCorreo, this)) {
                // El correo electrónico no es válido
                return@setOnClickListener
            }

            if (correoElectronicoExiste(correo)) {
                // El correo ya existe en la base de datos, muestra un mensaje de error o realiza alguna acción adecuada.
                Toast.makeText(this, "El correo electrónico ya está registrado.", Toast.LENGTH_SHORT).show()
            }
                val editor = sharedPrefs.edit()
                editor.putString("Codigo", codigoSeguridad)
                editor.apply()

                codigoSeguridad = (0..99999999).random().toString()
                val tituloCorreo = "Codigo de verificacion para correo electronico"
                val cuerpoCorreo =
                    "Querido usuario, este es un código de verificación. Por favor, ingréselo en el lugar adecuado en la aplicación. Si no puede ingresar con este código, puede solicitar que se le reenvíe uno nuevo."

                val task = SendMailTask(
                    txtCorreo.text.toString(),
                    tituloCorreo,
                    cuerpoCorreo + " Código: $codigoSeguridad"
                )
                task.execute()

                val intent = Intent(this, RegistroVerificarCorreoParte2::class.java)
                startActivity(intent)



        }



    }
    fun correoElectronicoExiste(correo: String): Boolean {
        try {
            val statement = connection.createStatement()
            val query = "SELECT COUNT(*) AS count FROM tbPersonas WHERE CorreoElectronico = ?"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, correo)
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val count = resultSet.getInt("count")
                return count > 0 // Si count es mayor que 0, significa que el correo ya existe.
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // Si ocurre una excepción o no se encuentra el correo, asumimos que no existe.
    }
}