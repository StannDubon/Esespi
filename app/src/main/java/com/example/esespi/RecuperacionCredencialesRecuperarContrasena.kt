package com.example.esespi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var txtContraseña:EditText
private lateinit var txtConfirmarContraseña: EditText
private lateinit var BtnConfirmar:Button
private lateinit var connection: Connection

class RecuperacionCredencialesRecuperarContrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_recuperar_contrasena)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        txtContraseña=findViewById(R.id.RecuperacionCredencialesRecuperarContraseñaTxtContraseña)
        txtConfirmarContraseña=findViewById(R.id.RecuperacionCredencialesRecuperarContraseñaTxtConfirmarContraseña)
        BtnConfirmar=findViewById(R.id.RecuperacionCredencialesRecuperarContraseñaBtnConfirmar)

        BtnConfirmar.setOnClickListener {
            try {
                // Preparar la sentencia SQL para la inserción de datos
                val sentencia = "EXEC CambiarContraseña ?, ?;"
                val preparedStatement: PreparedStatement = connection.prepareStatement(sentencia)

                // Asignar los valores a los parámetros de la sentencia preparada
                preparedStatement.setString(1, correo_usuario_a_recuperar)
                preparedStatement.setString(2, Encriptacion().convertirSHA256(txtConfirmarContraseña.text.toString()))

                // Ejecutar la sentencia de inserción
                preparedStatement.executeUpdate()

                println("Datos insertados correctamente en la base de datos.")
            }
            catch (e: SQLException) {
                e.printStackTrace()
                println("Error con la inserción de todos los datos");
            }
        }
    }
}