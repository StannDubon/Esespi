package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.SQLException

private lateinit var txtONI: EditText
private lateinit var txtNumeroPlaca: EditText
private lateinit var btnSiguiente: Button

class RegistroUsuarioDatosPolicia : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_datos_policia)

        txtONI=findViewById(R.id.RegistroUsuarioDatosPoliciaONI)
        txtNumeroPlaca=findViewById(R.id.RegistroUsuarioDatosPoliciaNumeroPlaca)
        btnSiguiente=findViewById(R.id.RegistroUsuarioDatosPoliciaSiguiente)

        btnSiguiente.setOnClickListener {
            val oni = txtONI.text.toString()
            val numeroPlaca = txtNumeroPlaca.text.toString()
            val v = Validaciones()

            if (v.CharWritten(txtONI, "ONI", 8, 8, this) &&
                v.CharWritten(txtNumeroPlaca, "Numero de Placa", 5, 5, this)
            ) {
                if (verificaExistenciaONIPlaca(oni, numeroPlaca, this)) {
                    // Ya existe un registro con el mismo ONI o número de placa, muestra un mensaje de error.
                    Toast.makeText(this, "El ONI o número de placa ya está registrado.", Toast.LENGTH_SHORT).show()

                } else {
                    // No existe un registro con el mismo ONI o número de placa, procede con el registro.
                    val RegistroUsuarioValoresDeRegistro =
                        getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                    val editor = RegistroUsuarioValoresDeRegistro.edit()
                    editor.putString("ONI", oni)
                    editor.putString("NumeroPlaca", numeroPlaca)
                    editor.apply()

                    val intent = Intent(this, RegistroUsuarioVerificarCorreo::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    // Esta función verifica si el ONI o el número de placa ya existen en la base de datos.
    fun verificaExistenciaONIPlaca(oni: String, numeroPlaca: String, context: Context): Boolean {
        try {
            val connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
            val query = "SELECT COUNT(*) AS count FROM tbPolicias WHERE ONI = ? OR NumeroPlaca = ?"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, oni)
            preparedStatement.setString(2, numeroPlaca)
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val count = resultSet.getInt("count")
                return count > 0 // Si count es mayor que 0, significa que ya existe un registro con ese ONI o número de placa.
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // Si ocurre una excepción o no se encuentra el ONI o número de placa, asumimos que no existe.
    }
}