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
private lateinit var connection: Connection
private lateinit var sharedPrefs: SharedPreferences
private lateinit var btnCorreoVerificar: Button

class RecuperacionCredencialesMailVerificacion : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_mail_verificacion)

        txtCorreo = findViewById(R.id.RecuperacionCredencialesMailVerificacionTxtEmail)
        btnCorreoVerificar = findViewById(R.id.btnCorreoVerifcar)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)

        btnCorreoVerificar.setOnClickListener {
            val correo = txtCorreo.text.toString().trim()
            val validaciones = Validaciones()

            if (validaciones.validarCorreoElectronico(txtCorreo, this)) {

                if (correoElectronicoExiste(correo)) {

                    val codigo = (0..99999999).random().toString()
                    val tituloCorreo = "Codigo de verificacion para correo electronico"
                    val cuerpoCorreo =
                        "Querido usuario, este es un código de verificación. Por favor, ingréselo en el lugar adecuado en la aplicación. Si no puede ingresar con este código, puede solicitar que se le reenvíe uno nuevo."

                    val task = SendMailTask(
                        txtCorreo.text.toString(),
                        tituloCorreo,
                        cuerpoCorreo + " Código: $codigo"
                    )
                    task.execute()

                    // Guardar el correo y el código en SharedPreferences
                    val editor = sharedPrefs.edit()
                    editor.putString("Correo", correo)
                    editor.putString("Codigo", codigo)
                    editor.apply()

                    val intent = Intent(this, RecuperacionCredencialesMailVerificacion2::class.java)
                    intent.putExtra("codigo", codigo) // Pasa el código como un extra
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "El correo electrónico no está registrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun correoElectronicoExiste(correo: String): Boolean {
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
