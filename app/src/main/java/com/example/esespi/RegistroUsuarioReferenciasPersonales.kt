package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

private lateinit var btnAñadirReferencia: Button
private lateinit var btnContinuar: Button
val userDataList: ArrayList<HashMap<String, String>> = ArrayList()

class RegistroUsuarioReferenciasPersonales : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_referencias_personales)

        btnContinuar=findViewById(R.id.RegistroUsuarioReferenciasPersonalesBtnContinuar)
        btnContinuar.setOnClickListener {
            val intent = Intent(this, RegistroUsuarioIngresoCredenciales::class.java)
            startActivity(intent)

        }

        btnAñadirReferencia = findViewById(R.id.RegistroUsuarioReferenciasPersonalesBtnAñadirReferencia)
        btnAñadirReferencia.setOnClickListener {

            val intent = Intent(this, RegistroUsuarioIngresoDatosReferenciaPersonal::class.java)
            startActivityForResult(intent, 3)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {

            val userData = data?.getSerializableExtra("userData") as? HashMap<String, String>
            val linLay =  findViewById<LinearLayout>(R.id.LinearLayoutReferenciasPersonales)

            if (userData != null) {
                val inflater = LayoutInflater.from(this)
                val tarjeta = inflater.inflate(R.layout.card_registro_usuario_referencia, linLay, false)

                val txtNombre: TextView = tarjeta.findViewById(R.id.RegistroUsuarioCardReferenciaTxtNombre)
                val txtCorreo: TextView = tarjeta.findViewById(R.id.RegistroUsuarioCardReferenciaTxtCorreo)
                val txtTelefono: TextView = tarjeta.findViewById(R.id.RegistroUsuarioCardReferenciaTxtTelefono)
                val txtDUI: TextView = tarjeta.findViewById(R.id.RegistroUsuarioCardReferenciaTxtDUI)

                val btnEditar: LinearLayout = tarjeta.findViewById(R.id.RegistroUsuarioCardReferenciaBtnEditar)
                val btnEliminar: LinearLayout = tarjeta.findViewById(R.id.RegistroUsuarioCardReferenciaBtnEliminar)

                txtNombre.text = userData["nombre"]
                txtCorreo.text = userData["correo"]
                txtTelefono.text = userData["telefono"]
                txtDUI.text = userData["dui"]

                btnEliminar.setOnClickListener {
                    linLay.removeView(tarjeta)
                    userDataList.remove(userData)
                }

                btnEditar.setOnClickListener {
                    val intent = Intent(this, RegistroUsuarioIngresoDatosReferenciaPersonal::class.java)
                    intent.putExtra("userData", userData)
                    intent.putExtra("mode", "Edit")
                    startActivityForResult(intent, 3)
                }

                linLay.addView(tarjeta)
                userDataList.add(userData)
            }
        }
    }

}