package com.example.esespi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var txtUsuario: EditText
private lateinit var txtConfirmarUsuario: EditText
private lateinit var BtnConfirmar: Button
private lateinit var connection: Connection

class RecuperacionCredencialesRecuperarUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_recuperar_usuario)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        txtUsuario=findViewById(R.id.RecuperacionCredencialesRecuperarUsuarioTxtUsuario)
        txtConfirmarUsuario=findViewById(R.id.RecuperacionCredencialesRecuperarUsuarioTxtConfirmarUsuario)
        BtnConfirmar=findViewById(R.id.RecuperacionCredencialesRecuperarUsuarioBtnAceptar)

        BtnConfirmar.setOnClickListener {
            try {
                // Preparar la sentencia SQL para la inserción de datos
                val sentencia = "EXEC CambiarUsuario ?, ?;"
                val preparedStatement: PreparedStatement = connection.prepareStatement(sentencia)

                // Asignar los valores a los parámetros de la sentencia preparada
                preparedStatement.setString(1, correo_usuario_a_recuperar)
                preparedStatement.setString(2, txtConfirmarUsuario.text.toString())

                // Ejecutar la sentencia de inserción
                preparedStatement.executeUpdate()

                println("Datos insertados correctamente en la base de datos.")
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
            catch (e: SQLException) {
                e.printStackTrace()
                println("Error con la inserción de todos los datos");
            }
        }
    }
}