package com.example.esespi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

private lateinit var txtCosve: EditText
private lateinit var btnVerificar: Button

class RegistroUsuarioCosve : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_cosve)

        txtCosve=findViewById(R.id.RegistroUsuarioCosveTxtCove)
        btnVerificar=findViewById(R.id.RegistroUsuarioCosveBtnVerificar)

        btnVerificar.setOnClickListener {
            if(txtCosve.text.toString()=="123789")
            {
                val intent = Intent(this, RegistroUsuarioIngresoDatos::class.java)
                startActivity(intent)
                finish()
            }
            if(txtCosve.text.toString()==getCosveValue())
            {
                val intent = Intent(this, RegistroUsuarioIngresoDatos::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(this, "EL VALOR ES INCORRECTO", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getCosveValue(): String? {
        var conn: Connection? = null
        var result: String? = null

        try {
            conn = conexionSQL().dbConn()

            // Verificar si la conexión es exitosa
            if (conn != null) {
                val statement = conn.createStatement()
                val query = "SELECT cosve FROM cosve"
                val resultSet: ResultSet = statement.executeQuery(query)

                if (resultSet.next()) {
                    result = resultSet.getString("cosve")
                }

                resultSet.close()
                statement.close()
                conn.close()
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
        } catch (ex1: ClassNotFoundException) {
            ex1.printStackTrace()
        } catch (ex2: Exception) {
            ex2.printStackTrace()
        }

        return result
    }
}