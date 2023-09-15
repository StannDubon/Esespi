package com.example.esespi

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
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

private lateinit var imgDetenido:ImageView
private lateinit var lblNombre:TextView
private lateinit var lblTipoDetencion:TextView
private lateinit var lblDui:TextView
private lateinit var lblFecha:TextView
private lateinit var lblDepartamento:TextView
private lateinit var lblMunicipio:TextView
private lateinit var lblDireccion:TextView

private lateinit var btnEliminar : LinearLayout
private lateinit var btnEditar : LinearLayout
private lateinit var btnRegresar : LinearLayout

class Detenidos_info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detenidos_info)

        imgDetenido = findViewById(R.id.Detenidos_info_imgDetenido)
        lblNombre = findViewById(R.id.Detenidos_info_lblNombre)
        lblTipoDetencion = findViewById(R.id.Detenidos_info_lblTipoDetencion)
        lblDui = findViewById(R.id.Detenidos_info_lblDui)
        lblFecha = findViewById(R.id.Detenidos_info_lblFecha)
        lblDepartamento = findViewById(R.id.Detenidos_info_lblDepartamento)
        lblMunicipio = findViewById(R.id.Detenidos_info_lblMunicipio)
        lblDireccion = findViewById(R.id.Detenidos_info_lblDireccion)

        btnEliminar = findViewById(R.id.Detenidos_info_btnEliminar)
        btnEditar = findViewById(R.id.Detenidos_info_btnEditar)
        btnRegresar = findViewById(R.id.Detenidos_info_btnRegresar)

        val Id = intent.getStringExtra("IdDetenido")
        val TipoDetencion = intent.getStringExtra("TipoDetencion")
        val Nombre = intent.getStringExtra("Nombre")
        val Fecha = intent.getStringExtra("Fecha")
        val Lugar = intent.getStringExtra("LugarDetencion")
        val Foto = intent.getByteArrayExtra("Foto")
        val Dui = intent.getStringExtra("Dui")

        println(Fecha)

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

        lblNombre.text = Nombre
        lblFecha.text = "$fecha $hora"
        lblDui.text = Dui
        lblTipoDetencion.text = TipoDetencion
        lblDepartamento.text = departamento
        lblMunicipio.text = municipio
        lblDireccion.text = direccion

        val bitmap = Foto?.let { BitmapFactory.decodeByteArray(Foto, 0, it.size) }
        imgDetenido.setImageBitmap(bitmap)

        var btnSalir = findViewById<ImageView>(R.id.Detenidos_info_btnQuit)
        btnSalir.setOnClickListener {
            finish()
        }


        btnEliminar.setOnClickListener {
            var con = conexionSQL().dbConn()
            if (con!=null){
                try {
                    val addProducto: PreparedStatement =  con.prepareStatement("EXEC dbo.EliminarDetenido ?;")!!
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
            val intent = Intent(this, Detenidos_agregar::class.java)
            intent.putExtra("mode", "Editar")
            intent.putExtra("IdDetenido", Id)
            intent.putExtra("TipoDetencion", TipoDetencion)
            intent.putExtra("Nombre", Nombre)
            intent.putExtra("Fecha", Fecha)
            intent.putExtra("LugarDetencion", Lugar)
            intent.putExtra("Foto", Foto)
            intent.putExtra("Dui", Dui)
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