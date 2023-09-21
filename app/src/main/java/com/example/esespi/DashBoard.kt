package com.example.esespi

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

private lateinit var AcercamientoCiudadano: LinearLayout
private lateinit var DenunciaCiudadana: LinearLayout
private lateinit var Decomiso: LinearLayout
private lateinit var Infractores: LinearLayout
private lateinit var Detenidos: LinearLayout

var IdGrupoGot:Int=0

class DashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        AcercamientoCiudadano=findViewById(R.id.DashBoard_btnAcercamientoCiudadano)
        DenunciaCiudadana=findViewById(R.id.DashBoard_btnDenunciaCiudadana)
        Decomiso=findViewById(R.id.DashBoard_btnDecomiso)
        Infractores=findViewById(R.id.DashBoard_btnInfractores)
        Detenidos=findViewById(R.id.DashBoard_btnDetenidos)

        IdGrupoGot=intent.getIntExtra("grupoPatrullaje", 0)

        AcercamientoCiudadano.setOnClickListener {
            val intent = Intent(this, Acercamientos_main::class.java)
            startActivity(intent)
        }

        DenunciaCiudadana.setOnClickListener {

        }

        Decomiso.setOnClickListener {
            val intent = Intent(this, Decomisos_main::class.java)
            startActivity(intent)
        }

        Infractores.setOnClickListener {
            val intent = Intent(this, Infractores_main::class.java)
            startActivity(intent)
        }

        Detenidos.setOnClickListener {
            val intent = Intent(this, Detenidos_main::class.java)
            startActivity(intent)
        }

    }
}