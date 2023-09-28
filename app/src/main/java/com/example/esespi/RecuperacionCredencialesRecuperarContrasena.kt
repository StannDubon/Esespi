package com.example.esespi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ctc.wstx.shaded.msv_core.writer.relaxng.Context
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var txtContraseña:EditText
private lateinit var txtConfirmarContraseña: EditText
private lateinit var BtnConfirmar:Button
private lateinit var connection: Connection
private lateinit var sharedPrefs: SharedPreferences

class RecuperacionCredencialesRecuperarContrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_recuperar_contrasena)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        txtContraseña=findViewById(R.id.RecuperacionCredencialesRecuperarContraseñaTxtContraseña)
        txtConfirmarContraseña=findViewById(R.id.RecuperacionCredencialesRecuperarContraseñaTxtConfirmarContraseña)
        BtnConfirmar=findViewById(R.id.RecuperacionCredencialesRecuperarContraseñaBtnConfirmar)

        // Recuperar el correo almacenado en SharedPreferences
        sharedPrefs = getSharedPreferences("datos_ingreso", android.content.Context.MODE_PRIVATE)
        val correoRecuperacion = sharedPrefs.getString("Correo", "")

        BtnConfirmar.setOnClickListener {
            var v = Validaciones()

                if (v.CharWritten(txtContraseña, "La Contraseña", 15, 8, this) &&
                    v.CharWritten(txtConfirmarContraseña, "La confirmacion de contraseña", 15, 8, this)) {

                    try {

                        if (txtContraseña.text.toString() != txtConfirmarContraseña.text.toString()) {
                            Toast.makeText(this, "Las contraseñas deben de ser iguales", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Cambio succesful", Toast.LENGTH_SHORT).show()
                            // Preparar la sentencia SQL para la inserción de datos
                            val sentencia = "EXEC ActualizarContra ?, ?;"
                            val preparedStatement: PreparedStatement =
                                connection.prepareStatement(sentencia)

                            // Asignar los valores a los parámetros de la sentencia preparada
                            preparedStatement.setString(1, correoRecuperacion)
                            preparedStatement.setString(
                                2,
                                Encriptacion().convertirSHA256(txtConfirmarContraseña.text.toString())
                            )

                            // Ejecutar la sentencia de inserción
                            preparedStatement.executeUpdate()

                            println("Datos insertados correctamente en la base de datos.")

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: SQLException) {
                        e.printStackTrace()
                        println("Error con la inserción de todos los datos");
                    }

                }


        }
    }
}