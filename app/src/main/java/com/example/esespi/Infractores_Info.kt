package com.example.esespi

import android.annotation.SuppressLint
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

private lateinit var imgInfractor : ImageView
private lateinit var lblNombre : TextView
private lateinit var lblDui : TextView
private lateinit var lblGenero : TextView
private lateinit var lblLastSeen : TextView
private lateinit var lblDescripcion : TextView
private lateinit var lblDepartamento : TextView
private lateinit var lblMunicipio : TextView
private lateinit var lblDireccion : TextView

private lateinit var btnEliminar : LinearLayout
private lateinit var btnEditar : LinearLayout
private lateinit var btnRegresar : LinearLayout


class Infractores_Info : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infractores_info)

        imgInfractor = findViewById(R.id.Infractores_info_imgInfractor)
        lblNombre = findViewById(R.id.Infractores_info_lblNombre)
        lblDui = findViewById(R.id.Infractores_info_lblDui)
        lblGenero = findViewById(R.id.Infractores_info_lblGenero)
        lblLastSeen = findViewById(R.id.Infractores_info_lblLastSeen)
        lblDescripcion = findViewById(R.id.Infractores_info_lblDescripcion)
        lblDepartamento = findViewById(R.id.Infractores_info_lblDepartamento)
        lblMunicipio = findViewById(R.id.Infractores_info_lblMunicipio)
        lblDireccion = findViewById(R.id.Infractores_info_lblDireccion)

        btnEliminar = findViewById(R.id.Infractores_info_btnEliminar)
        btnEditar = findViewById(R.id.Infractores_info_btnEditar)
        btnRegresar = findViewById(R.id.Infractores_info_btnRegresar)

        val Id = intent.getStringExtra("id")
        val Nombre = intent.getStringExtra("nom")
        val Apellido = intent.getStringExtra("ape")
        val DUI = intent.getStringExtra("dui")
        val Direccion = intent.getStringExtra("dir")
        val IdGenero = intent.getIntExtra("gen", 0)
        val Descripcion = intent.getStringExtra("des")
        val UltimaVezVisto = intent.getStringExtra("las")
        val Foto = intent.getByteArrayExtra("img")

        val DireccionFull = Direccion?.split(", ")

        val departamento = DireccionFull?.get(0)
        val municipio = DireccionFull?.get(1)
        val direccion = DireccionFull?.drop(2)?.joinToString(", ")

        if(IdGenero==1){
            lblGenero.text = "Masculino"
        }else if (IdGenero==2)
        {
            lblGenero.text = "Femenino"
        }
        else{
            lblGenero.text = "No identificado"
        }

        lblNombre.text = "$Nombre $Apellido"
        lblDui.text = DUI
        lblLastSeen.text = UltimaVezVisto
        lblDescripcion.text = Descripcion
        lblDepartamento.text = departamento
        lblMunicipio.text = municipio
        lblDireccion.text = direccion

        var btnSalir = findViewById<ImageView>(R.id.Infractores_info_btnQuit)
        btnSalir.setOnClickListener {
            finish()
        }

        val bitmap = Foto?.let { BitmapFactory.decodeByteArray(Foto, 0, it.size) }
        imgInfractor.setImageBitmap(bitmap)

        btnEliminar.setOnClickListener {
            var con = conexionSQL().dbConn()
            if (con!=null){
                try {
                    val addProducto: PreparedStatement =  con.prepareStatement("EXEC dbo.EliminarInfractor @IdPersona = ?;")!!
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
                    onBackPressed()
                }
            }
        }

        btnRegresar.setOnClickListener {
            finish()
        }
        btnEditar.setOnClickListener {
            val intent = Intent(this, Infractores_agregar::class.java)
            intent.putExtra("mode", "Editar")

            intent.putExtra("id", Id)
            intent.putExtra("nom", Nombre)
            intent.putExtra("ape", Apellido)
            intent.putExtra("dui", DUI)
            intent.putExtra("dir", Direccion)
            intent.putExtra("gen", IdGenero)
            intent.putExtra("des", Descripcion)
            intent.putExtra("las", UltimaVezVisto)
            intent.putExtra("img", Foto)
            startActivityForResult(intent, 1)
            finish()
        }

    }

    override fun onBackPressed() {
        //setResult(Activity.RESULT_OK, Intent())
        super.onBackPressed()
        finish()
    }
}