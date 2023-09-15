package com.example.esespi

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

private lateinit var btnQuit:ImageView

private lateinit var btnTomarFoto:LinearLayout
private lateinit var btnSubirFoto:LinearLayout
private lateinit var imgDecomiso:ImageView

private lateinit var txtNombre:EditText
private lateinit var txtDescripcion:EditText

private lateinit var LlInfractoresSeleccionados:LinearLayout
private lateinit var LlAgregarInvolucrado:LinearLayout

private lateinit var btnDescartar:LinearLayout
private lateinit var btnGuardar:LinearLayout

class Decomisos_agregar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decomisos_agregar)

        /*
        btnQuit = findViewById(R.id.Decomisos_agregar_btnQuitTop)

        btnTomarFoto = findViewById(R.id.Decomisos_agregar_btnTomarFoto)
        btnSubirFoto = findViewById(R.id.Decomisos_agregar_btnSubirFoto)
        imgDecomiso = findViewById(R.id.Decomisos_agregar_imgDecomiso)

        txtNombre = findViewById(R.id.Decomisos_agregar_txtNombre)
        txtDescripcion = findViewById(R.id.Decomisos_agregar_txtDescripcion)

        LlInfractoresSeleccionados = findViewById(R.id.Decomisos_agregar_LlInfractoresSelected)
        LlAgregarInvolucrado = findViewById(R.id.Decomisos_agregar_btnSeleccionarInfractor)

        btnDescartar = findViewById(R.id.Decomisos_agregar_btnDescartar)
        btnGuardar = findViewById(R.id.Decomisos_agregar_btnGuardar)

         */

    }
}