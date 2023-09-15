package com.example.esespi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar

private lateinit var lblNombre : TextView
private lateinit var lblFecha : TextView
private lateinit var lblMotivo : TextView
private lateinit var lblDepartamento : TextView
private lateinit var lblMunicipio : TextView
private lateinit var lblDireccion : TextView

private lateinit var btnEliminar : LinearLayout
private lateinit var btnEditar : LinearLayout
private lateinit var btnRegresar : LinearLayout

class Acercamientos_info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acercamientos_info)

        lblNombre = findViewById(R.id.Acercamientos_info_lblNombre)
        lblFecha = findViewById(R.id.Acercamientos_info_lblFecha)
        lblMotivo = findViewById(R.id.Acercamientos_info_lblMotivo)
        lblDepartamento = findViewById(R.id.Acercamientos_info_lblDepartamento)
        lblMunicipio = findViewById(R.id.Acercamientos_info_lblMunicipio)
        lblDireccion = findViewById(R.id.Acercamientos_info_lblDireccion)

        btnEliminar = findViewById(R.id.Acercamientos_info_btnEliminar)
        btnEditar = findViewById(R.id.Acercamientos_info_btnEditar)
        btnRegresar = findViewById(R.id.Acercamientos_info_btnRegresar)

        val Id = intent.getStringExtra("id")
        val IdInforme = intent.getStringExtra("inf")
        val Lugar = intent.getStringExtra("lug")
        val Fecha = intent.getStringExtra("fec")
        val NombrePersona = intent.getStringExtra("nomp")
        val Acercamiento = intent.getStringExtra("ace")

        val DireccionFull = Lugar?.split(", ")

        val departamento = DireccionFull?.get(0)
        val municipio = DireccionFull?.get(1)
        val direccion = DireccionFull?.drop(2)?.joinToString(", ")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateTimeString = Fecha
        val calendar = Calendar.getInstance()

        // Parsear el valor datetime
        calendar.time = dateFormat.parse(dateTimeString)

        // Obtener la fecha y la hora sin segundos
        val fecha = SimpleDateFormat("yyyy-MM-dd").format(calendar.time) // Separar la fecha
        val hora = SimpleDateFormat("hh:mm a").format(calendar.time) // Separar la hora con AM/PM

        lblNombre.text = NombrePersona
        lblFecha.text = "$fecha $hora"
        lblMotivo.text = Acercamiento.toString()
        lblDepartamento.text = departamento
        lblMunicipio.text = municipio
        lblDireccion.text = direccion


        var btnSalir = findViewById<ImageView>(R.id.Infractores_info_btnQuit)
        btnSalir.setOnClickListener {
            finish()
        }


        btnEliminar.setOnClickListener {
            var con = conexionSQL().dbConn()
            if (con!=null){
                try {
                    val addProducto: PreparedStatement =  con.prepareStatement("DELETE FROM tbAcercamientos WHERE IdAcercamiento = ?;")!!
                    addProducto.setString(1, Id)
                    addProducto.executeUpdate()
                    Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                    con.close()

                    onBackPressed()
                }
                catch (ex: SQLException){
                    Toast.makeText(this, "Ocurrio un error: "+ex, Toast.LENGTH_SHORT).show()
                    println(ex)
                    con.close()
                }
            }
        }

        btnRegresar.setOnClickListener {
            finish()
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, Acercamientos_agregar::class.java)
            intent.putExtra("mode", "Editar")
            intent.putExtra("id", Id)
            intent.putExtra("inf", IdInforme)
            intent.putExtra("lug", Lugar)
            intent.putExtra("fec", Fecha)
            intent.putExtra("nomp", NombrePersona)
            intent.putExtra("ace", Acercamiento)
            startActivityForResult(intent, 1)
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent())
        super.onBackPressed()
        finish()
    }
}