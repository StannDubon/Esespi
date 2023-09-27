package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.renderscript.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.*

private lateinit var connSQL: conexionSQL
private lateinit var Usuario: EditText
private lateinit var Contraseña: EditText
private lateinit var Login: Button
private lateinit var Registrarse: Button
private lateinit var CambiarCredenciales:TextView

private var GrupoPatrullaje:Int=1

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Usuario=findViewById(R.id.txtUsuarioLogIn)
        Contraseña=findViewById(R.id.txtContraseñaLogIn)
        Login=findViewById(R.id.btnIniciarSesiónLogIn)
        Registrarse=findViewById(R.id.btnIniciarSesionRegistrarse)
        CambiarCredenciales= findViewById(R.id.LogInCambiarCredenciales)
        connSQL = conexionSQL()

        val sharedPreferences = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Elimina todos los datos almacenados en SharedPreferences
        editor.apply()

        CambiarCredenciales.setOnClickListener {
            val intent = Intent(this, RecuperacionCredencialesElegirMetodo::class.java)
            startActivity(intent)
        }

        Registrarse.setOnClickListener {

            val sharedPreferences = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear() // Elimina todos los datos almacenados en SharedPreferences
            editor.apply()

            val intent = Intent(this, RegistroUsuarioCosve::class.java)
            intent.putExtra("grupoPatrullaje", GrupoPatrullaje)
            startActivity(intent)
        }

        Login.setOnClickListener {
            val usuario = Usuario.text.toString()
            val contraseña = Encriptacion().convertirSHA256(Contraseña.text.toString())

            val conn = connSQL.dbConn()

            var v = Validaciones()


            if (conn != null) {
                val credentialsValid = verifyCredentials(conn, usuario, contraseña)
                if(v.CharWritten(Contraseña, "La contraseaña", 15,8,this)){

                }

                if (credentialsValid == true) {
                    showToast("Inicio de sesión exitoso")

                    val login = Intent(this, DashBoard::class.java)
                    login.putExtra("grupoPatrullaje" , GrupoPatrullaje)
                    startActivity(login)

                } else {
                    showToast("Credenciales inválidas")
                }

                conn.close()
            } else {
                showToast("Error de conexión")
            }

        }

    }

    private fun verifyCredentials(conn: Connection, usuario: String, contraseña: String): Boolean {
        val query = "SELECT COUNT(*) FROM tbUsuarios WHERE Usuario = ? AND Contraseña = ?"
        val preparedStatement = conn.prepareStatement(query)
        preparedStatement.setString(1, usuario)
        preparedStatement.setString(2, contraseña)

        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val count = resultSet.getInt(1)

        resultSet.close()
        preparedStatement.close()

        return count > 0
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}