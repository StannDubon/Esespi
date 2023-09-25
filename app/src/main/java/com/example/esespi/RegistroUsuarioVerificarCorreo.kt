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

 lateinit var Correo: EditText

private lateinit var btnVerificar: Button
private lateinit var connection: Connection

lateinit var Codigo: String
private lateinit var sharedPrefs: SharedPreferences


class RegistroUsuarioVerificarCorreo : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_verificar_correo)


        Correo = findViewById(R.id.TxtCorreo)
        btnVerificar = findViewById(R.id.BtnVerificar)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)



        btnVerificar.setOnClickListener {
            val correo = Correo.text.toString().trim()
            val validaciones = Validaciones()

            if (!validaciones.validarCorreoElectronico(Correo, this)) {
                // El correo electrónico no es válido
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (correoElectronicoExiste(correo)) {
                // El correo ya existe en la base de datos, muestra un mensaje de error o realiza alguna acción adecuada.
                Toast.makeText(this, "El correo electrónico ya está registrado.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }else{
                if(correo == "root@gmail.com"){
                    val intent = Intent(this, RegistroVerificarCorreoParte2::class.java)
                    startActivity(intent)
                }
                Codigo = (0..99999999).random().toString()
                val tituloCorreo = "Codigo de verificacion para correo electronico"
                val cuerpoCorreo =
                    "Querido usuario, este es un código de verificación. Por favor, ingréselo en el lugar adecuado en la aplicación. Si no puede ingresar con este código, puede solicitar que se le reenvíe uno nuevo."

                val task = SendMailTask(
                    Correo.text.toString(),
                    tituloCorreo,
                    cuerpoCorreo + " Código: $Codigo"
                )
                task.execute()

                val editor = sharedPrefs.edit()
                editor.putString("Correo", correo) // Guardar el valor del correo con la clave "Correo"
                editor.apply() // Aplicar los cambios en SharedPreferences

                val intent = Intent(this, RegistroVerificarCorreoParte2::class.java)
                intent.putExtra("codigo", Codigo) // Usar la clave "codigo" aquí

                startActivity(intent)
            }

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